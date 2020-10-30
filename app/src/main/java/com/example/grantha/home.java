package com.example.grantha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

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
        //for restricting to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        /*//detect dynamic link
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
                        getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId",referLink).apply();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetail()).commit();




                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });*/

        bottomNavigationView=findViewById(R.id.bottom_navigation);

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
                        SharedPreferences preferences=getSharedPreferences("PROFILE",0);
                        preferences.edit().remove("profileId").commit();
                        selectorFragment=new profileFragment();
                        break;
                }
                if (selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).addToBackStack(null).commit();

                }
                return true;

            }
        });
        //when redirected from comment activity
       /* Bundle intent =getIntent().getExtras();
        if(intent!=null){

            String profileId=intent.getString("publisherId");
            //using shared preferences
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new profileFragment()).commit();
            //change the selected fragment at bottom bar
            bottomNavigationView.setSelectedItemId(R.id.nav_person);

        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new homeFragment()).commit();
        }*/



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
                        if (deepLink != null) {
                            referLink = deepLink.toString();
                            try {
                                //extract postId from link
                                referLink = referLink.substring(referLink.lastIndexOf("=") + 1);
                            } catch (Exception e) {
                                // error
                            }


                            // Handle the deep link. open the linked
                            // content, or apply promotional credit to the user's
                            // account.

                            //open postDetail activity of specific post
                            getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", referLink).apply();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetail()).commit();


                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }
    
     public void createlink(String title, String post) {

       DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.com.example.grantha/home"))
                .setDomainUriPrefix("https://granthaapp.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();




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


    public void downloadPdf(String title, final String imageUrl, String article)

    {
        PdfDocument pdfDocument= new PdfDocument();
        final Paint paint =new Paint();
        PdfDocument.PageInfo pageInfo =new PdfDocument.PageInfo.Builder(450,700,1).create();
        final PdfDocument.Page page =pdfDocument.startPage(pageInfo);
        final Canvas canvas =page.getCanvas();

        paint.setTextSize(15.5f);
        paint.setColor(Color.BLACK);
        canvas.drawText("GRANTHA ARTICLE", 175,20,paint);
        paint.setTextSize(12f);
        paint.isUnderlineText();
        paint.getFontFeatureSettings();
        canvas.drawText(title,20,40,paint);
        Spanned spanned = Html.fromHtml(article,1);
        char[] chars = new char[spanned.length()];
        TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
        String plainText = new String(chars);

        paint.setTextSize(8f);
        TextPaint textPaint=new TextPaint();
        textPaint.getStyle();
        StaticLayout staticLayout = StaticLayout.Builder
                .obtain(plainText, 0, plainText.length(),textPaint, 400)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build();
        canvas.translate(20, 60);
        staticLayout.draw(canvas);

        canvas.isHardwareAccelerated();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeStream((new URL(imageUrl))
                    .openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap resized = Bitmap.createScaledBitmap(bm, bm.getWidth()/4, bm.getHeight()/4, true);
        canvas.drawBitmap(resized,10,staticLayout.getHeight()+40,null);

        pdfDocument.finishPage(page);
        File file =new File(this.getExternalFilesDir("/"),title+".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        Toast.makeText(getApplicationContext(),"PDF downloaded!",Toast.LENGTH_LONG).show();
    }


    private void status(String status)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String,Object> map=new HashMap<>();

        map.put("status",status);
        ref.updateChildren(map);

    }

    @Override
    protected void onResume() {

        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            status("online");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            status("offline");
        }

    }









}
