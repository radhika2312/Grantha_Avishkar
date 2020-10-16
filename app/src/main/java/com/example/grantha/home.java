package com.example.grantha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import Fragments.homeFragment;
import Fragments.notificationFragment;
import Fragments.profileFragment;
import Fragments.searchFragment;

public class home extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    String TAG="home";
    String referLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        //detect dynamic link
         FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                         if(deepLink!=null)
                        referLink=deepLink.toString();
                        try {
                             //extract postId from link
                            referLink=referLink.substring(referLink.lastIndexOf("=")+1);
                        }catch (Exception e)
                        {
                            // error
                        }

                        // Handle the deep link. open the linked
                        // content, or apply promotional credit to the user's
                        // account.

                        //open postDetail activity of specific post
                        getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",referLink).apply();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetail()).commit();




                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        selectorFragment=new homeFragment();
                        break;
                    case R.id.nav_fav:
                        selectorFragment=new notificationFragment();
                        break;
                    case R.id.nav_add:
                        selectorFragment=null;
                        startActivity(new Intent(home.this,postActivity.class));
                        break;
                    case R.id.nav_search:
                        selectorFragment=new searchFragment();
                        break;
                    case R.id.nav_person:
                        selectorFragment=new profileFragment();
                        break;
                }
                if (selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();

                }
                return true;

            }
        });
        //when redirected from comment activity
        Bundle intent =getIntent().getExtras();
        if(intent!=null){

            String profileId=intent.getString("publisherId");
            //using shared preferences
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new profileFragment()).commit();
            //change the selected fragment at bottom bar
            bottomNavigationView.setSelectedItemId(R.id.nav_person);

        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new homeFragment()).commit();
        }
    }
    
     public void createlink(String title, String url,String post) {

       /* DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.com.example.grantha/home"))
                .setDomainUriPrefix("https://granthaapp.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        */


       String shareLinkText= "https://granthaapp.page.link/?"+
               "link=https://www.com.example.grantha/home?postid="+post+
               "&apn="+getPackageName()+
               "&st="+"Check this article"+
               "&sd="+title;

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(shareLinkText))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = Objects.requireNonNull(task.getResult()).getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        } else {
                            // Error
                        }
                    }



                });

    }

    
}
