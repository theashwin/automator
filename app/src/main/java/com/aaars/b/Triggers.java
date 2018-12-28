package com.aaars.b;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.aaars.b.Fragments.QuotesReceiver;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;


import java.util.HashMap;
import java.util.Random;

import static com.aaars.b.Root.USER_ID;


public class Triggers extends Service implements MessageListener{
    Module md1, md2, md3, md4, md5, md6, md7, md8;
    LastRun lr;
    long[] lastrun = new long[50];
    Module[] md;
    DatabaseReference[] dr;
    DatabaseReference drmd1, drmd2, drmd3, drmd4, drmd5, drmd6, last;
    HashMap<String, Boolean> booleanMap;
    SmsManager smsManager;
    LocationManager locationManager;
    Calendar start, end;
    Boolean begin = true;



    public Triggers() {

    }

    @Override
    public void messageReceived(String message) {
        Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        booleanMap = new HashMap<>();
        smsManager = SmsManager.getDefault();

        md = new Module[20];
        dr = new DatabaseReference[20];


        MessageReceiver.bindListener(this);

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //LASTRUN
        last = database.getInstance().getReference().child("users").child(USER_ID).child("lastrun");
        ValueEventListener lastr = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    lr = dataSnapshot.getValue(LastRun.class);
                }
                else {
                    last.setValue(lr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        last.addValueEventListener(lastr);


        //LOW BATTERY SMS LOCATION - MODULE 01
        drmd1 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("1");
        ValueEventListener lowbatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md1 = dataSnapshot.getValue(Module.class);
                    booleanMap.put("lowBatteryLocation", md1.enabled);
                    booleanMap.put("onConnectDisconnectToast",true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd1.addValueEventListener(lowbatListener);

        //WIFI OFF TIMER - MODULE 02
        drmd2 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("2");
        ValueEventListener wifiTimerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md2 = dataSnapshot.getValue(Module.class);
                    booleanMap.put("wifiTimer", md2.enabled);
                    wifitimer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd2.addValueEventListener(wifiTimerListener);

        //WIFI ON TIMER - MODULE 03
        dr[4] = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("4");
        ValueEventListener geoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md[4] = dataSnapshot.getValue(Module.class);
                    booleanMap.put("geofencing", md[4].enabled);
                    geotimer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr[4].addValueEventListener(geoListener);

        //GEO WIFI ON TIMER - MODULE 03
        dr[5] = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("11");
        ValueEventListener geofListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md[5] = dataSnapshot.getValue(Module.class);
                    booleanMap.put("geowifi", md[5].enabled);
                    geooff();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr[5].addValueEventListener(geofListener);

        //QUOTES EMAIL - MODULE 05
        drmd5 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("5");
        ValueEventListener quotesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md5 = dataSnapshot.getValue(Module.class);
                    booleanMap.put("quotesEmail", md5.enabled);
                    quotesEmail();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd5.addValueEventListener(quotesListener);

        //5 HELP - MODULE 10
        drmd6 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("10");
        ValueEventListener helpListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md6 = dataSnapshot.getValue(Module.class);
                    booleanMap.put("askHelp", md6.enabled);
                    askHelp();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd6.addValueEventListener(helpListener);

        //XKCD - MODULE 11
        dr[7] = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("7");
        ValueEventListener xkcdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md[7] = dataSnapshot.getValue(Module.class);
                    booleanMap.put("xkcd", md[7].enabled);
                    quotesEmail();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr[7].addValueEventListener(xkcdListener);

        //RSS - MODULE 11
        dr[8] = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("8");
        ValueEventListener rssListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md[8] = dataSnapshot.getValue(Module.class);
                    booleanMap.put("rss", md[8].enabled);
                    quotesEmail();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr[8].addValueEventListener(xkcdListener);

        batteryTrigger();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Triggers.this);
        notificationManager.cancel(3);
    }

    public void callNotification(String title, String text) {
        Intent intent = new Intent(Triggers.this, Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(Triggers.this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Triggers.this, "default")
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setSmallIcon(R.drawable.alpha)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Triggers.this);
        notificationManager.notify(1, mBuilder.build());
    }

    public void callStickyNotification() {
        Intent intent = new Intent(Triggers.this, HelpSMS.class);
        String s = md6.parameters.get(0) + md6.parameters.get(1) + md6.parameters.get(2);
        intent.putExtra("number",s);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(Triggers.this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Triggers.this, "default")
                .setContentTitle("Emergency Location Sender")
                .setContentText("Tap on this Notification to send Location to trusted contacts")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Tap on this Notification to send Location to trusted contacts"))
                .setSmallIcon(R.drawable.alpha)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAutoCancel(false)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Triggers.this);
        notificationManager.notify(1, mBuilder.build());
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, "default")
                    .setAutoCancel(true)
                    .setOngoing(true);

            Notification notification = builder.build();
            startForeground(3, notification);
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Triggers.this, "default")
                    .setAutoCancel(true)
                    .setOngoing(true);

            Notification notification = builder.build();
            startForeground(3, notification);
        }
        return START_STICKY;
    }

    public void batteryTrigger() {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    //LOW BATTERY SMS LOCATION - MODULE 01
                    if (booleanMap.get("lowBatteryLocation")) {
                        if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                            final LocationListener locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(final Location location) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference("users").child(USER_ID).child("modules");
                                    ref.child("1").child("currentLocation").setValue(location);
                                    Calendar cc = Calendar.getInstance();
                                    lr.lastrun.set(1,"" + cc.getTime());
                                    last.setValue(lr);
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) { }

                                @Override
                                public void onProviderEnabled(String s) { }

                                @Override
                                public void onProviderDisabled(String s) { }
                            };

                            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15,
                                        50, locationListener);

                            Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            String content = "I am here: https://www.google.com/maps/search/?api=1&query=" + lc.getLatitude() + "," + lc.getLongitude() + " - Sent via Automator" ;
                            smsManager.sendTextMessage(md1.parameters.get(0),null,content,null,null);
                            callNotification("A Module Ran - Low Battery","Your current location sent to your trusted number for security purpose.");
                            Calendar cc = Calendar.getInstance();
                            lr.lastrun.set(1,"" + cc.getTime());
                            last.setValue(lr);
                        }
                    }

                    //SEND TOAST ON CHARGING CONNECT / DISCONNECT - MODULE *
                    if (booleanMap.get("onConnectDisconnectToast")) {

                        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                            Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
                            Calendar cc = Calendar.getInstance();
                            lr.lastrun.set(1,"" + cc.getTime());
                            last.setValue(lr);
                        } else {
                            intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
                            Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
                            Calendar cc = Calendar.getInstance();
                            lr.lastrun.set(1,"" + cc.getTime());
                            last.setValue(lr);
                        }
                    }
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);
    }

    //WIFI OFF TIMER - MODULE 02 - [FUNCTION CALL]
    public void wifitimer() {
        try {
            if (booleanMap.get("wifiTimer")) {
                Intent alarm = new Intent(getApplicationContext(), TriggerReceiver.class);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(md2.parameters.get(7)));
                c.set(Calendar.MINUTE, Integer.parseInt(md2.parameters.get(8)));

                if (Calendar.getInstance().after(c)) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), 999685, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
    }

    //WIFI ON TIMER - MODULE 03 - [FUNCTION CALL]
    public void wifiontimer(){
        try {
            if (booleanMap.get("wifiOnTimer")) {
                Intent alarmtwo = new Intent(getApplicationContext(), TriggerOnReceiver.class);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(md3.parameters.get(7)));
                c.set(Calendar.MINUTE, Integer.parseInt(md3.parameters.get(8)));

                if (Calendar.getInstance().after(c)) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }

                PendingIntent pendingIntenttwo = PendingIntent.getBroadcast(
                        getApplicationContext(), 999687, alarmtwo, 0);
                AlarmManager alarmManagertwo = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManagertwo.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmManagertwo.INTERVAL_DAY, pendingIntenttwo);
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
    }

    //QUOTES EMAIL - MODULE 05 - [FUNCTION CALL]
    public void quotesEmail() {
        try {
            if (booleanMap.get("quotesEmail") || booleanMap.get("xkcd") || booleanMap.get("rss")) {
                Intent alarmthree = new Intent(getApplicationContext(), QuotesReceiver.class);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, 10);
                c.set(Calendar.MINUTE, 0);

                if (Calendar.getInstance().after(c)) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }

                PendingIntent pendingIntentthree = PendingIntent.getBroadcast(
                        getApplicationContext(), 999686, alarmthree, 0);
                AlarmManager alarmManagerthree = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManagerthree.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmManagerthree.INTERVAL_DAY, pendingIntentthree);
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
    }

    public void askHelp(){
        if(booleanMap.get("askHelp")){
            callStickyNotification();
        }
        else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Triggers.this);
            notificationManager.cancel(1);
        }
    }

    public float distFrom(double lat1, double lng1, double lat2, double lng2) {
        float dist = 0;
        try {
            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(lat2 - lat1);
            double dLng = Math.toRadians(lng2 - lng1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            dist = (float) (earthRadius * c);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
        return dist;
    }


    void geotimer() {
        try {
            if (booleanMap.get("geofencing")) {
                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        if (distFrom(Double.parseDouble(md[4].parameters.get(0)), Double.parseDouble(md[4].parameters.get(1)), location.getLatitude(), location.getLongitude()) < 50) {
                            if (begin) {
                                start = Calendar.getInstance();
                                begin = false;
                            }
                            end = Calendar.getInstance();
                            if ((end.getTimeInMillis() - start.getTimeInMillis()) > 60000) {
                                long secs = Long.parseLong(md[4].parameters.get(2)) + (end.getTimeInMillis() - start.getTimeInMillis());
                                md[4].parameters.set(2, Long.toString(secs));
                                dr[4].setValue(md[4]);
                                start = end;
                            }
                        } else {
                            if (!begin) {
                                end = Calendar.getInstance();
                                long secs = Long.parseLong(md[4].parameters.get(2)) + (end.getTimeInMillis() - start.getTimeInMillis());
                                md[4].parameters.set(2, Long.toString(secs));
                                dr[4].setValue(md[4]);
                                begin = true;
                                Calendar cc = Calendar.getInstance();
                                lr.lastrun.set(4,"" + cc.getTime());
                                last.setValue(lr);
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                    }

                    @Override
                    public void onProviderEnabled(String s) {
                    }

                    @Override
                    public void onProviderDisabled(String s) {
                    }
                };

                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15,
                            50, locationListener);
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
    }


    void geooff() {
        try {
            if (booleanMap.get("geowifi")) {
                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        if (distFrom(Double.parseDouble(md[5].parameters.get(0)), Double.parseDouble(md[5].parameters.get(1)), location.getLatitude(), location.getLongitude()) < 250) {
                            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            if (wifiManager != null) {
                                wifiManager.setWifiEnabled(true);
                                callNotification("Wi-Fi turned on", "Wi-Fi was turned on entering location");
                                Calendar cc = Calendar.getInstance();
                                lr.lastrun.set(5,"" + cc.getTime());
                                last.setValue(lr);
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                    }

                    @Override
                    public void onProviderEnabled(String s) {
                    }

                    @Override
                    public void onProviderDisabled(String s) {
                    }
                };

                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15,
                            50, locationListener);
                Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (lc != null) {
                    if (distFrom(Double.parseDouble(md[5].parameters.get(0)), Double.parseDouble(md[5].parameters.get(1)), lc.getLatitude(), lc.getLongitude()) < 500) {
                        if (wifiManager != null) {
                            wifiManager.setWifiEnabled(true);
                            callNotification("Wi-Fi turned on", "Wi-Fi was turned on entering location");
                            Calendar cc = Calendar.getInstance();
                            lr.lastrun.set(5,"" + cc.getTime());
                            last.setValue(lr);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
    }
}