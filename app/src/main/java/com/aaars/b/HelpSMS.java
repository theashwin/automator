package com.aaars.b;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.aaars.b.Root.USER_ID;

public class HelpSMS extends AppCompatActivity {
    SmsManager smsManager, smsManagerbeta, smsManagergamma;
    LocationManager locationManager;
    Module md6;
    DatabaseReference drmd6;
    Boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sms);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //5 HELP - MODULE 10
        drmd6 = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("10");
        ValueEventListener helpListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md6 = dataSnapshot.getValue(Module.class);
                    if(flag)
                        call(md6.parameters.get(0),md6.parameters.get(1),md6.parameters.get(2));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        drmd6.addValueEventListener(helpListener);

    }

    void call(String a, String b, String c) {
        try {
            flag = false;
            smsManager = SmsManager.getDefault();

            final LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("users").child(USER_ID).child("modules");
                    ref.child("10").child("currentLocation").setValue(location);
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

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15,
                        50, locationListener);

            Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String content = "I am here: https://www.google.com/maps/search/?api=1&query=" + lc.getLatitude() + "," + lc.getLongitude() + " - Sent via Automator";
            if (a.length() == 10)
                smsManager.sendTextMessage(a, null, content, null, null);
            if (b.length() == 10)
                smsManager.sendTextMessage(b, null, content, null, null);
            if (c.length() == 10)
                smsManager.sendTextMessage(c, null, content, null, null);
            finish();
            System.exit(0);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Module Ran, Restart app to sync with server", Toast.LENGTH_LONG).show();
        }
    }
}
