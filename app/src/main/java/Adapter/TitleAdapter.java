package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.EditArticle;
import com.example.grantha.PostDetail;
import com.example.grantha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.Notification;
import Model.Post;
import Model.User;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mArticles;
    private List<String> mUserIds;

    private FirebaseUser firebaseUser;
    private String admin;

    public TitleAdapter(Context mContext, List<Post> mArticles) {
        this.mContext = mContext;
        this.mArticles = mArticles;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.title_item, parent,false);
        return new TitleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Post post=mArticles.get(position);

        FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                admin=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.title.setText(post.getTitle());

        if(firebaseUser.getUid().equals(post.getPublisher()) || firebaseUser.getUid().equals(admin)){
            holder.btnEdit.setVisibility(View.VISIBLE);
        }else{
            holder.btnEdit.setVisibility(View.GONE);
        }

        //redirecting to full view of that article
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",post.getPostId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PostDetail()).commit();

            }
        });

        //redirecting to edit artcile activity..
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext, EditArticle.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);

                /*mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",post.getPostId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new EditArticles()).commit();*/

            }
        });


        //deleting articles
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (post.getPublisher().equals(firebaseUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            final String deleteId=post.getPostId();

                            deletePost(deleteId);
                            deleteBook(deleteId);


                            //making list of present users
                            final List<User> users=new ArrayList<>();
                            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snap1:snapshot.getChildren()){
                                        User user1=snap1.getValue(User.class);
                                        users.add(user1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            //trying a query
                            /*for(User u:users){
                                FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(u.getId())
                                        .child(post.getPostId()).removeValue();
                            }*/


                            FirebaseDatabase.getInstance().getReference().child("Notifications").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(final DataSnapshot snap2:snapshot.getChildren()){
                                        final String key2=snap2.getKey();
                                        Toast.makeText(mContext,key2,Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference().child("Notifications").child(key2).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot Snapshot) {
                                                for(DataSnapshot snap3:Snapshot.getChildren()){
                                                    Notification notify=snap3.getValue(Notification.class);
                                                    if(notify.getPostid().equals(deleteId)){
                                                        FirebaseDatabase.getInstance().getReference().child("Notifications")
                                                                .child(key2).child(notify.getId()).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }



                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            //deleting data of article from likes and comments section
                            //FirebaseDatabase.getInstance().getReference().child("Likes").child(deleteId).removeValue();
                            //FirebaseDatabase.getInstance().getReference().child("Comments").child(deleteId).removeValue();




                        }
                    });
                    alertDialog.show();

                }
                return true;
            }
        });



    }

    private void deleteBook(final String deleteId) {
        //deleting data of articles from bookmarks
        final List<String> keys=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Bookmarks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    String k=snap.getKey();
                    //Toast.makeText(mContext,k,Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(k).child(deleteId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deletePost(final String deleteId) {
        //deleting articles from post section
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren())
                {
                    Post post1=snap.getValue(Post.class);
                    if(post1.getPostId().equals(deleteId)){
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(post1.getPostId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public Button btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            btnEdit=itemView.findViewById(R.id.btn_edit);
        }
    }



}
