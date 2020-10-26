package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import Model.CodeAndDecode;
import Model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private Context mContext;
    private List<Message> mChat;

    private FirebaseUser fUser;

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    public MessageAdapter(Context mContext, List<Message> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message=mChat.get(position);

        final String secretKey = "grantha!radhika!poorvi";
        String encryptedString = message.getText();
        String decryptedString = CodeAndDecode.decrypt(encryptedString, secretKey) ;


        holder.mssg.setText(decryptedString);
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mssg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           mssg=itemView.findViewById(R.id.show_mssgs);
        }
    }

    @Override
    public int getItemViewType(int position){
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }


}
