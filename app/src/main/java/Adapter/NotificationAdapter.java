package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.PostDetail;
import com.example.grantha.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Fragments.profileFragment;
import Model.Notification;
import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification=mNotifications.get(position);

        //to get user info
        getUser(holder.imageProfile,holder.username,notification.getUserid());

        holder.comment.setText(notification.getText());

        //if notification is a post
        if(notification.getIsPost().equals("true")){
            holder.postImage.setVisibility(View.VISIBLE);
            //get pic of post
            getPostImage(holder.postImage,notification.getPostid());
        }else{
            holder.postImage.setVisibility(View.GONE);
        }

        //redirecting to post or user
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getIsPost().equals("true")){
                    mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                            .putString("postId",notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new PostDetail()).commit();
                }else{
                    mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                            .putString("profileId",notification.getUserid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new profileFragment()).commit();
                }

            }
        });

    }



    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView comment;
        public ImageView postImage;
        public CircleImageView imageProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
            postImage=itemView.findViewById(R.id.post_image);
            imageProfile=itemView.findViewById(R.id.image_profile);

        }
    }

    //function for getting image of post
    private void getPostImage(final ImageView postImage, final String postid) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //function for getting user info
    private void getUser(final CircleImageView imageProfile, final TextView username, final String userid) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                }
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
