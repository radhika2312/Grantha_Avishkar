package com.example.grantha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.ChatUserAdapter;
import Model.User;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatUserAdapter chatUserAdapter;
    private List<User> userList;

    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList=new ArrayList<>();
        chatUserAdapter=new ChatUserAdapter(this,userList);
        recyclerView.setAdapter(chatUserAdapter);

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    User user1=snap.getValue(User.class);
                    if(!user1.getId().equals(fUser.getUid())){
                        userList.add(user1);
                    }
                }
                chatUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String,Object> map=new HashMap<>();

        map.put("status",status);
        ref.updateChildren(map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}