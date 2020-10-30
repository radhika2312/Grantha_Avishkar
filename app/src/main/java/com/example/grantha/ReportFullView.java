package com.example.grantha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.PreviewAdapter;
import Model.Post;

public class ReportFullView extends AppCompatActivity {

    private ImageView close;
    private RecyclerView recyclerView;
    private PreviewAdapter previewAdapter;
    private List<Post> articlesList;

    private String articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_full_view);
        //for restricting to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        close=findViewById(R.id.close);

        Intent intent=getIntent();
        articleId=intent.getStringExtra("postId");

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articlesList=new ArrayList<>();
        previewAdapter=new PreviewAdapter(this,articlesList);
        recyclerView.setAdapter(previewAdapter);

        FirebaseDatabase.getInstance().getReference().child("Posts").child(articleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articlesList.clear();
                Post post=snapshot.getValue(Post.class);
                articlesList.add(post);
                previewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}