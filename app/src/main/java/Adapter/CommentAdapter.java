package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.example.grantha.home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Comment;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    String postId;
    private List<Comment> mComments;
    private FirebaseUser fUser;

    public CommentAdapter(Context mContext, List<Comment> mComments,String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId=postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=mComments.get(position);
        holder.comment.setText(comment.getComment());
        //not knowing the name of publisher directly ..but having publisher id,,,so wud query to the db

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //on clicking on comment or profile image..redirecting to profile of that user
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, home.class);
                intent.putExtra("publisherId",comment.getPublisher());
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);

            }
        });
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,home.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });


        //deleting comments
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().equals(fUser.getUid())) {
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
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                                    .child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "comment deleted successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }


                            });
                        }
                    });
                    alertDialog.show();

                }
                return true;
            }
        });



    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView comment;
        public TextView username;
        public CircleImageView imageProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment=itemView.findViewById(R.id.comment);
            username=itemView.findViewById(R.id.username);
            imageProfile=itemView.findViewById(R.id.image_profile);
        }
    }




}
