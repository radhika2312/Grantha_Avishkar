package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.example.grantha.ReportFullView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.Notification;
import Model.Post;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mArticles;
    private Post post;

    public ReportAdapter(Context mContext, List<Post> mArticles) {
        this.mContext = mContext;
        this.mArticles = mArticles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.report_item,parent,false);
        return new ReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post=mArticles.get(position);



        //setting xml components
        holder.title.setText(post.getTitle());
        //setting up a reference pointer
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("ReportAbuse").child(post.getPostId());
        //counting no of spams
        ref.child("spam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.spam.setText(snapshot.getChildrenCount()+" Spams report");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //counting no. of inappropriate
        ref.child("inappropriate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.inappropriate.setText(snapshot.getChildrenCount()+" inappropriate content");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //directing to full view of article
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ReportFullView.class);
                intent.putExtra("postId",post.getPostId());
                //intent.putExtra("path","ReportAbuse");
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
            }
        });

        //deleting report
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String deleteId=post.getPostId();

                FirebaseDatabase.getInstance().getReference().child("Notifications").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1:snapshot.getChildren()) {
                            Notification noti = snapshot1.getValue(Notification.class);
                            if (noti.getPostid().equals(deleteId)) {
                                FirebaseDatabase.getInstance().getReference().child("Notifications").child(noti.getId()).removeValue();
                            }
                        }
                        //notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                deletePost(deleteId);
                deleteBook(deleteId);






                /*FirebaseDatabase.getInstance().getReference().child("Notifications").addValueEventListener(new ValueEventListener() {
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
                });*/


                FirebaseDatabase.getInstance().getReference().child("ReportAbuse").child(deleteId).removeValue();








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
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(deleteId).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView title;
        public ImageView reject;
        public TextView spam;
        public TextView inappropriate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            reject=itemView.findViewById(R.id.reject);
            spam=itemView.findViewById(R.id.spam);
            inappropriate=itemView.findViewById(R.id.inappropriate);

        }
    }

}
