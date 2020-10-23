package Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.ArticleAdapter;
import Adapter.UserAdapter;
import Model.Post;
import Model.User;


public class searchFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView recyclerView_articles;
    private List<User> mUsers;
    private List<Post> mArticles;
    private SocialAutoCompleteTextView search_bar;
    private UserAdapter userAdapter;
    private ArticleAdapter articleAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers=new ArrayList<>();
        userAdapter=new UserAdapter(getContext(),mUsers,true);
        recyclerView.setAdapter(userAdapter);
        //for articles
        recyclerView_articles=view.findViewById(R.id.recycler_view_articles);
        recyclerView_articles.setHasFixedSize(true);
        recyclerView_articles.setLayoutManager(new LinearLayoutManager(getContext()));

        mArticles=new ArrayList<>();
        articleAdapter=new ArticleAdapter(getContext(),mArticles);
        recyclerView_articles.setAdapter(articleAdapter);




        search_bar=view.findViewById(R.id.search_bar);




        readUsers();
        readArticles();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                searchUsers(s.toString());
                searchArticles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchArticles(editable.toString());

            }
        });

        return view;
    }

    private void searchArticles(String s) {
        Query query=FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("title")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mArticles.clear();
                for(DataSnapshot Snapshot : snapshot.getChildren()){
                    Post post=Snapshot.getValue(Post.class);
                    mArticles.add(post);

                }
                articleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readArticles() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("POsts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    mArticles.clear();
                    for(DataSnapshot Snapshot:snapshot.getChildren()){
                        Post post=Snapshot.getValue(Post.class);
                        mArticles.add(post);
                    }
                    articleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot Snapshot: snapshot.getChildren()){
                        User user=Snapshot.getValue(User.class);
                        mUsers.add(user);

                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUsers(String s){
        Query query=FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot Snapshot : snapshot.getChildren()){
                    User user=Snapshot.getValue(User.class);
                    mUsers.add(user);

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}