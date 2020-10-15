package com.example.grantha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.CommentAdapter;
import Model.Comment;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private EditText addComment;
    private CircleImageView imageProfile;
    private TextView post;
    private String postId;
    private String authorId;

    FirebaseUser fUser;

    private RecyclerView recyclerView;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");



        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList,postId);
        recyclerView.setAdapter(commentAdapter);


        addComment=findViewById(R.id.add_comment);
        imageProfile=findViewById(R.id.image_profile);
        post=findViewById(R.id.post);


        fUser= FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString()))
                {
                    Toast.makeText(CommentActivity.this,"NO comment added",Toast.LENGTH_SHORT).show();
                }else{
                    putComment();
                }
            }
        });
        //to get comments
        getComment();


    }

    private void getComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot Snapshot:snapshot.getChildren()){

                    Comment comment=Snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment() {
        HashMap<String,Object> map=new HashMap<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id=ref.push().getKey();
        map.put("id",id);
        map.put("comment",addComment.getText().toString());
        map.put("publisher",fUser.getUid());
        addComment.setText("");
        ref.child(id).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CommentActivity.this,"comment added",Toast.LENGTH_SHORT).show();
                            //adding notification
                            HashMap<String ,Object> map=new HashMap<>();

                            map.put("userid",fUser.getUid());
                            map.put("text","commented on your article");
                            map.put("postid",postId);
                            map.put("isPost","true");

                            FirebaseDatabase.getInstance().getReference().child("Notifications").child(authorId)
                                    .push().setValue(map);
                        }else{
                            Toast.makeText(CommentActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        if(user.getImageurl().equals("default")){
                            imageProfile.setImageResource(R.mipmap.ic_launcher);
                        }else{
                            Picasso.get().load(user.getImageurl()).into(imageProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




}