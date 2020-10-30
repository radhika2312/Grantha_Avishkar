package com.example.grantha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserAdapter;
import Model.User;

public class FollowersActivity extends AppCompatActivity {

    private String id;
    private String title;
    private List<String> idList;
    private ImageView close;
    private TextView heading;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        //for restricting to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        //setting up toolbar...this time in java activity instead of xml
        Toolbar toolbar =findViewById(R.id.toolbar);
        close=findViewById(R.id.close);
        heading=findViewById(R.id.title);
        //setSupportActionBar(toolbar);
        if(title.equals("likes")){
            heading.setText("Upvotes");
            //getSupportActionBar().setTitle("Upvotes");
        }else{
            heading.setText(title);
            // getSupportActionBar().setTitle(title);
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //going back to the activity from where it started..
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //setting up recycler view
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers=new ArrayList<>();
        userAdapter=new UserAdapter(this,mUsers,false);
        recyclerView.setAdapter(userAdapter);

        //initiallisig id list
        idList=new ArrayList<>();

        switch (title){
            case "followers":
                getFollowers();
                break;

            case "followings":
                getFollowings();
                break;

            case "likes":
                getLikes();
                break;
        }
    }

    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for(DataSnapshot Snap:snapshot.getChildren()){
                            idList.add(Snap.getKey());
                        }
                        showUsers();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void getFollowings() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("following")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot Snap:snapshot.getChildren()){
                    idList.add(Snap.getKey());
                }
                showUsers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getLikes() {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                //idList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                for(DataSnapshot Snap:snapshot.getChildren()){
                    idList.add(Snap.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot Snap:snapshot.getChildren()){
                    User user =Snap.getValue(User.class);
                    for(String id1:idList){
                        if(user.getId().equals(id1)){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}