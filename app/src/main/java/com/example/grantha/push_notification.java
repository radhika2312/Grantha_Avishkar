package com.example.grantha;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class push_notification extends IntentService {

	private int saved1=0;
	private int count1=0;
	private int saved2=0;
	private int count2=0;
	private String loggedIn;

	public push_notification() {
		super("push_notification");
	}


	@Override
	protected void onHandleIntent(Intent intent) {

        Log.e("Info","Reached Here");
		WakefulBroadcastReceiver.completeWakefulIntent(intent);
		loadUser();
		if(loggedIn.equals("granthaapp@gmail.com")) {
			DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PermissionNeeded");
			ref.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					loadPreferences();
					count1 = 0;
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          count1++;
					}
					Log.e("count", "Saved: " + saved1 + " Count: " + count1);
					if (saved1 < count1) {
						Log.e("Info", "Reached Here too");
						createNotificationChannel();
						notification("PERMISSION NEEDED, NEW POST ADDED");
					}
					clearPreferences();
					savePreferences(count1);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
				}
			});
			final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ReportAbuse");
			ref1.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					loadPreferences2();
					count2 = 0;
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         	count2++;


					}
					Log.e("count", "Saved: " + saved2 + " Count: " + count2);
					if (saved2 < count2) {
						Log.e("Info", "Reached Here too");
						createNotificationChannel();
						notification("NEW POST REPORTED");
					}
					clearPreferences2();
					savePreferences2(count2);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
				}


			});



		}
	}

	private void createNotificationChannel() {

		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel("New Post", "New Post", importance);
			channel.enableVibration(true);
			channel.enableLights(true);
			channel.setShowBadge(true);
			channel.setDescription("GRANTHA");


			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}


	private void notification(String title) {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "New Post")

				.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
				.setSmallIcon(R.mipmap.ic_app_icon_round)
				.setAutoCancel(true)
				.setContentTitle("New Post")
				.setContentText(title)
				.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText("Unverified Post Added, tap to login..."))
				.setContentIntent(pendingintent);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//		notificationId is a unique int for each notification that you must define
		notificationManager.notify(12, builder.build());
	}

	private void loadPreferences()
	{
		SharedPreferences sharedPreferences=getSharedPreferences("new notification",MODE_PRIVATE);
		saved1=Integer.parseInt(sharedPreferences.getString("Number","0"));
	}

	private void savePreferences(int value)
	{
		SharedPreferences sharedPreferences=getSharedPreferences("new notification",MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("Number", ""+value);
		editor.apply();
	}

	private void clearPreferences()
	{
		SharedPreferences preferences = getSharedPreferences("new notification", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	private void loadPreferences2()
	{
		SharedPreferences sharedPreferences=getSharedPreferences("new notification-",MODE_PRIVATE);
		saved2=Integer.parseInt(sharedPreferences.getString("Number","0"));

	}

	private void savePreferences2(int value)
	{
		SharedPreferences sharedPreferences=getSharedPreferences("new notification-",MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("Number", ""+value);
		editor.apply();
	}

	private void clearPreferences2()
	{
		SharedPreferences preferences = getSharedPreferences("new notification-", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	private void loadUser()
	{
		SharedPreferences sharedPreferences=getSharedPreferences("usersave",MODE_PRIVATE);
		loggedIn=sharedPreferences.getString("User","no");
		if(loggedIn.equals("") || loggedIn.isEmpty() || loggedIn.equals("no"))
			loggedIn="no";
	}
}
