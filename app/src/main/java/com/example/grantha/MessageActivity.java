package com.example.grantha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.MessageAdapter;
import Model.Message;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView userImage;
    private TextView name;
    private ImageButton btnSend;
    private TextView txtSend;

    private String userId;
    private FirebaseUser fUser;

    private RecyclerView recyclerView;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");

        userImage=findViewById(R.id.user_image);
        name=findViewById(R.id.name);
        txtSend=findViewById(R.id.txt_send);
        btnSend=findViewById(R.id.btn_send);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageList=new ArrayList<>();
        messageAdapter=new MessageAdapter(this,messageList);
        recyclerView.setAdapter(messageAdapter);

        readMessages(fUser.getUid(),userId);

        //setting up toolbar
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user2=snapshot.getValue(User.class);
                name.setText(user2.getName());
                if(user2.getImageurl().equals("default")){
                    userImage.setImageResource(R.drawable.animate);
                }else{
                    Picasso.get().load(user2.getImageurl()).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //seding messages
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSend.getText().toString().equals("")){
                    Toast.makeText(MessageActivity.this,"Empty Message",Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("sender",userId);
                    map.put("receiver",fUser.getUid());
                    map.put("text",txtSend.getText().toString());

                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(map);

                    txtSend.setText("");

                }
            }
        });


    }

    private void readMessages(final String uid, final String userId) {
        FirebaseDatabase.getInstance().getReference().child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Message chat=snap.getValue(Message.class);
                    if((chat.getSender().equals(uid) && chat.getReceiver().equals(userId))
                            ||(chat.getReceiver().equals(uid)&&chat.getSender().equals(userId))){
                        messageList.add(chat);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}