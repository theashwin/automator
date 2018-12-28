package com.aaars.b;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.aaars.b.Root.USER_ID;

public class TriggerOnReceiver extends BroadcastReceiver {
    Module md2;

    @Override
    public void onReceive(final Context context, Intent intent) {

        try {
            md2 = new Module();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference drmd2 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("3");
            ValueEventListener wifiTimerListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        md2 = dataSnapshot.getValue(Module.class);
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());

                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        int weekDay = 0;

                        if (Calendar.MONDAY == dayOfWeek) weekDay = 0;
                        else if (Calendar.TUESDAY == dayOfWeek) weekDay = 1;
                        else if (Calendar.WEDNESDAY == dayOfWeek) weekDay = 2;
                        else if (Calendar.THURSDAY == dayOfWeek) weekDay = 3;
                        else if (Calendar.FRIDAY == dayOfWeek) weekDay = 4;
                        else if (Calendar.SATURDAY == dayOfWeek) weekDay = 5;
                        else if (Calendar.SUNDAY == dayOfWeek) weekDay = 6;


                        if (md2.parameters.get(weekDay).equals("true")) {
                            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            if (wifiManager != null) {
                                wifiManager.setWifiEnabled(true);
                                callNotification("A Module Ran - WiFi Turned ON", "WiFi was turned ON!", context);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            drmd2.addValueEventListener(wifiTimerListener);
        }
        catch(Exception e) {
            Toast.makeText(context, "Module ran, Restart app to sync", Toast.LENGTH_LONG).show();
        }
    }

    public void callNotification(String title, String text, Context context) {
        Intent intent = new Intent(context, Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setSmallIcon(R.drawable.alpha)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, mBuilder.build());
    }
}
