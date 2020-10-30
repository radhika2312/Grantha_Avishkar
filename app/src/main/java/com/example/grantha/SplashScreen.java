package com.example.grantha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		//for restricting to portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getSupportActionBar().hide();

		Thread background = new Thread() {
			public void run() {
				try {
					// Thread will sleep for 2 seconds
					sleep(2*1000);

					// After 2 seconds redirect to another intent
					Intent i=new Intent(getBaseContext(),MainActivity.class);
					startActivity(i);

					//Remove activity
					finish();
				} catch (Exception e) {
				}
			}
		};
		// start thread
		background.start();
	}
}
