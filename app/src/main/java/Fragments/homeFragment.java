package Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.ChatActivity;
import com.example.grantha.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.PostAdapter;
import Model.Post;


public class homeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;

    private PostAdapter postAdapter;
    private List<Post> postList;

    private ImageView chat;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //for sorting menu
        setHasOptionsMenu(true);

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewPosts=view.findViewById(R.id.recycler_view_posts);
        chat=view.findViewById(R.id.chat);
        //recyclerViewPosts.setAdapter(postAdapter);
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerViewPosts.setAdapter(postAdapter);

        //recyclerViewPosts.setAdapter(postAdapter);
        readPosts();

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChatActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    //sort feature
    //sort menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.sort,menu);
        super.onCreateOptionsMenu(menu,inflater);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_sortLike:
                Collections.sort(postList,Post.SortbyLikes);
                Toast.makeText(getContext(),"sort by likes",Toast.LENGTH_SHORT).show();
                postAdapter.notifyDataSetChanged();
                return true;

            case R.id.menu_sortTitle:
                Collections.sort(postList,Post.SortbyTitle);
                Toast.makeText(getContext(),"sort by title", Toast.LENGTH_SHORT).show();
                postAdapter.notifyDataSetChanged();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot Snapshot:snapshot.getChildren()){
                    Post post=Snapshot.getValue(Post.class);
                    postList.add(post);

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}