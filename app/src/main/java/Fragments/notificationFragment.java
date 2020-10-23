package Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grantha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private int count;

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

                    FirebaseDatabase.getInstance().getReference("ReportAbuse").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                                notification("Reported Abuse");

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {


                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                    FirebaseDatabase.getInstance().getReference("PermissionNeeded").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                                notification("Permission Needed");

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {


                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot Snap :snapshot.getChildren()){
                            notificationList.add(Snap.getValue(Notification.class));
                        }
                        Collections.reverse(notificationList);
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void notification(String title) {
        String NOTIFICATION_CHANNEL_ID = "example.grantha.postActivity";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager=getContext().getSystemService(NotificationManager.class);
            notificationChannel.setDescription("Grantha");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(),NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_action_home)
                .setContentTitle(title)
                .setContentText("new post added")
                .setContentInfo("Info")
        ;

        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(getContext());

        managerCompat.notify(count,notificationBuilder.build());
        count++;

    }

}