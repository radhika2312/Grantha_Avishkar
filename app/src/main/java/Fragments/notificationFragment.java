package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.NotificationAdapter;
import Adapter.PermissionAdapter;
import Adapter.ReportAdapter;
import Model.Notification;
import Model.Post;


public class notificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    //for permissting articles toi be posted by admin..
    private RecyclerView recyclerViewPermit;
    private PermissionAdapter permissionAdapter;
    private List<Post> articlesList;

    //for report abusing
    private RecyclerView recyclerViewReport;
    private ReportAdapter reportAdapter;
    private List<Post> reportList;

    //private List<String> idList;

    private FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);

        fUser=FirebaseAuth.getInstance().getCurrentUser();

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(notificationAdapter);

        //setting up for taking permission..
        recyclerViewPermit=view.findViewById(R.id.recycler_view_permit);
        recyclerViewPermit.setHasFixedSize(true);
        recyclerViewPermit.setLayoutManager(new LinearLayoutManager(getContext()));
        articlesList=new ArrayList<>();
        permissionAdapter=new PermissionAdapter(getContext(),articlesList,"New Article");
        recyclerViewPermit.setAdapter(permissionAdapter);

        //setting up for report abusing purpose..
        recyclerViewReport=view.findViewById(R.id.recycler_view_report);
        recyclerViewReport.setHasFixedSize(true);
        recyclerViewReport.setLayoutManager(new LinearLayoutManager(getContext()));
        reportList=new ArrayList<>();
        reportAdapter=new ReportAdapter(getContext(),reportList);
        recyclerViewReport.setAdapter(reportAdapter);

        //idList=new ArrayList<>();

        readNotifications();
        readArticles();
        readReports();

        return view;
    }

    private void readReports() {
        final List<String> idList= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String admin=snapshot.getValue().toString();
                if(fUser.getUid().equals(admin)){
                    FirebaseDatabase.getInstance().getReference().child("ReportAbuse").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            idList.clear();
                            for(DataSnapshot snap:snapshot.getChildren()){

                                String id1=snap.getKey().toString();
                                idList.add(id1);
                                //Toast.makeText(getContext(),id,Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    reportList.clear();
                    FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reportList.clear();
                            for(DataSnapshot snap:snapshot.getChildren()){
                                Post post=snap.getValue(Post.class);
                                for(String id:idList){
                                    if(post.getPostId().equals(id)){
                                        reportList.add(post);
                                    }
                                }
                            }
                            reportAdapter.notifyDataSetChanged();
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
    }

    private void readArticles() {
        FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String admin=snapshot.getValue().toString();
                if(fUser.getUid().equals(admin)){
                    FirebaseDatabase.getInstance().getReference().child("PermissionNeeded").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            articlesList.clear();
                            for(DataSnapshot Snap:snapshot.getChildren()){
                                Post post=Snap.getValue(Post.class);
                                articlesList.add(post);
                            }
                            permissionAdapter.notifyDataSetChanged();
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
    }

    private void readNotifications() {
        final List<Post> posts=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s:snapshot.getChildren()){
                    Post post=s.getValue(Post.class);
                    posts.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Notifications").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();
                        for(DataSnapshot Snap :snapshot.getChildren()){

                            final Notification noti1=Snap.getValue(Notification.class);
                            if(noti1.getIsPost().equals("false") && noti1.getReceiver().equals(fUser.getUid()))
                            {
                                notificationList.add(noti1);
                            }else if(noti1.getReceiver().equals(fUser.getUid())){

                                for(Post p:posts)
                                {
                                    if(noti1.getPostid().equals(p.getPostId()))
                                    {
                                        notificationList.add(noti1);
                                    }
                                }

                            }


                        }
                        Collections.reverse(notificationList);
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}