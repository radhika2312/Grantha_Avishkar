package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.PostDetail;
import com.example.grantha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Model.Post;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mArticles;

    private FirebaseUser firebaseUser;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post=mArticles.get(position);

        holder.title.setText(post.getTitle());

        if(firebaseUser.getUid().equals(post.getPublisher())){
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
                            FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "article deleted successfully", Toast.LENGTH_SHORT).show();
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
