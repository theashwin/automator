package com.aaars.b;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Random;

public class Splash extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        createNotificationChannel();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        progressBar = findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 100) {
                    progressStatus += new Random().nextInt(15);
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(50);    //SET TO ARBITRARY NUMBER
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent onbIntent = new Intent(Splash.this, Onboarding.class);
                Splash.this.startActivity(onbIntent);
                finish();
            }
        }).start();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //MIN
            int importanceLow = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channelLow = new NotificationChannel("default", "Automator", importanceLow);
            channelLow.setDescription("Automator Notification Channel");

            //HIGH PRIORITY
            int importanceHigh = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channelHigh = new NotificationChannel("high", "Automator", importanceHigh);
            channelHigh.setDescription("Automator Notification Channel");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channelLow);
            notificationManager.createNotificationChannel(channelHigh);
        }
    }
}
