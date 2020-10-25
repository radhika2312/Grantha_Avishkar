package Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.EditProfile;
import com.example.grantha.FollowersActivity;
import com.example.grantha.MainActivity;
import com.example.grantha.R;
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

import Adapter.TitleAdapter;
import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class profileFragment extends Fragment {

    private RecyclerView recyclerView;
    private TitleAdapter titleAdapter;//change to article adapter another
    private List<Post> myArticleList;

    private RecyclerView recyclerViewSaves;
    private TitleAdapter saveAdapter;//change to artcile adapter another
    private List<Post> mySavedArticleList;


    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullname;
    private TextView bio;
    private TextView interest;
    private TextView username;
    private ImageButton myArticles;
    private ImageButton mySavedArticles;

    private Button editProfile;

    String profileId;

    private FirebaseUser fUser;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        //checking for other users
        String data=getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                .getString("profileId","none");
        if(data.equals("none")){
            profileId=fUser.getUid();
        }else{
            profileId=data;
        }
        //data="";

        imageProfile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        interest=view.findViewById(R.id.interest);
        username=view.findViewById(R.id.username);
        myArticles=view.findViewById(R.id.my_artilces);
        mySavedArticles=view.findViewById(R.id.saved_articles);
        editProfile=view.findViewById(R.id.edit_profile);

        //for recycler views artciles...

        recyclerView=view.findViewById(R.id.recycler_view_articles);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myArticleList=new ArrayList<>();
        titleAdapter=new TitleAdapter(getContext(),myArticleList);
        recyclerView.setAdapter(titleAdapter);

        recyclerViewSaves=view.findViewById(R.id.recycler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new LinearLayoutManager(getContext()));
        mySavedArticleList=new ArrayList<>();
        saveAdapter=new TitleAdapter(getContext(),mySavedArticleList);
        recyclerViewSaves.setAdapter(saveAdapter);

        userInfo();
        getFollowersAndFollowingsCount();
        getPostCount();
        myArticles();
        getSavedPosts();



        if(profileId.equals(fUser.getUid()))
        {
            editProfile.setText("Edit Profile");
        }
        else {
            checkFollowingStatus();
            //savedPosts.setVisibility(View.GONE);

        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProfile.getText().toString();
                if (btn.equals("Edit Profile")) {
                    //go to edit Profile
                    startActivity(new Intent(getContext(), EditProfile.class));

                } else if (btn.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    //user----username
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(fUser.getUid()).setValue(true);

                    //adding notifications
                    HashMap<String ,Object> map=new HashMap<>();
                    //adding id..
                    DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("Notifications").child(profileId);
                    String id1=ref1.push().getKey();
                    map.put("userid",fUser.getUid());
                    map.put("text","started following you");
                    map.put("postid","");
                    map.put("isPost","false");
                    map.put("id",id1);

                    ref1.child(id1).setValue(map);


                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(profileId).removeValue();
                    //user----username
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(fUser.getUid()).removeValue();

                }


            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);

            }
        });

        mySavedArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });



        //showing list of followers..
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });

        //showing list of followings..
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followings");
                startActivity(intent);
            }
        });


        //logout
        options.setOnClickListener(new View.OnClickListener() {
            AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
            @Override
            public void onClick(View v) {

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();

                    }
                });
                alertDialog.show();
            }


        });

        return view;
    }

    private void myArticles() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myArticleList.clear();
                for(DataSnapshot Snap:snapshot.getChildren()){
                    Post post=Snap.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        myArticleList.add(post);
                    }
                }
                //Collections.reverse(myArticleList);
                titleAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //for bookmarks
    private void getSavedPosts() {
        final List<String> savedIds= new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot Snap:snapshot.getChildren()){
                    savedIds.add(Snap.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mySavedArticleList.clear();
                        for(DataSnapshot Snap1:snapshot.getChildren()){
                            Post post=Snap1.getValue(Post.class);
                            for(String id:savedIds){
                                if(post.getPostId().equals(id)){
                                    mySavedArticleList.add(post);
                                }
                            }
                        }
                        saveAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void userInfo()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);

                //Picasso.get().load(user.getImageurl()).into(image_profile);
                Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageProfile);


                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
                interest.setText(user.getInterest());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkFollowingStatus(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(fUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileId).exists())
                    editProfile.setText("following");
                else
                    editProfile.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getFollowersAndFollowingsCount(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void getPostCount()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId))
                        i++;
                }
                posts.setText(String.valueOf(i));
                //posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}