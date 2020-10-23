package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.example.grantha.ReportFullView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

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
               FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(mContext,"Article deleted!",Toast.LENGTH_SHORT).show();
                       }
                   }
               });
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
