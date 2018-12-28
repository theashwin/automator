package com.aaars.b.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aaars.b.Module;
import com.aaars.b.ModuleDesign;
import com.aaars.b.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.LOCATION_SERVICE;
import static com.aaars.b.Root.USER_ID;


public class LocationWiFi extends Fragment {

    TextView tv, tvhead;
    Switch toggle;
    EditText lat, lon;
    Button getloc, saveloc;
    String USER_ID;

    public Module md;
    DatabaseReference dr;
    LocationManager locationManager;


    public LocationWiFi() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_fencing, container, false);

        USER_ID = ModuleDesign.USER_ID;
        md = new Module();

        tvhead = view.findViewById(R.id.tvhead);
        tv = view.findViewById(R.id.tv);
        toggle = view.findViewById(R.id.toggle);
        lat = view.findViewById(R.id.inputLat);
        lon = view.findViewById(R.id.inputLong);
        getloc = view.findViewById(R.id.save);
        saveloc = view.findViewById(R.id.savebeta);
        TextView paraphrase = view.findViewById(R.id.paraphrase);

        paraphrase.setVisibility(View.GONE);
        tvhead.setText("Turn on Wi-Fi after entering a location");
        tv.setText("Wi-Fi will be turned on after entering a location to connect to faster internet seamlessly");

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("11");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md = dataSnapshot.getValue(Module.class);
                    toggle.setChecked(md.enabled);
                    if(!md.parameters.get(0).equals("0.0") && !md.parameters.get(1).equals("0.0")) {
                        lat.setText(md.parameters.get(0));
                        lon.setText(md.parameters.get(1));
                        lon.setEnabled(false);
                        lat.setEnabled(false);
                        getloc.setText("Get Location");
                        saveloc.setText("Edit Location");
                    }
                }
                else {
                    //INSTANTIATION OF MODULE 01
                    md.triggerid = 101;
                    md.activityid = 101;
                    md.enabled = true;
                    md.parameters.add("0.0");
                    md.parameters.add("0.0");
                    md.parameters.add("0");
                    dr.setValue(md);
                    getloc.setText("Get Location");
                    saveloc.setText("Set Location");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr.addValueEventListener(postListener);

        saveloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lat.isEnabled() && lon.isEnabled()) {
                    String latitude = lat.getText().toString();
                    String longitude = lon.getText().toString();
                    if (!latitude.isEmpty() && !longitude.isEmpty()) {
                        md.parameters.set(0,latitude);
                        md.parameters.set(1,longitude);
                        dr.setValue(md);
                        lon.setEnabled(false);
                        lat.setEnabled(false);
                        getloc.setText("Get Location");
                        saveloc.setText("Edit Location");
                    }
                }
                else {
                    lon.setEnabled(true);
                    lat.setEnabled(true);
                    getloc.setText("Get Location");
                    saveloc.setText("Set Location");
                }
            }
        });

        getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("users").child(USER_ID).child("modules");
                        ref.child("1").child("currentLocation").setValue(location);
                        lat.setText(Double.toString(location.getLatitude()));
                        lon.setText(Double.toString(location.getLongitude()));
                        getloc.setText("Get Location");
                        saveloc.setText("Edit Location");
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) { }

                    @Override
                    public void onProviderEnabled(String s) { }

                    @Override
                    public void onProviderDisabled(String s) { }
                };

                locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15,
                            50, locationListener);

                Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lat.setText(Double.toString(lc.getLatitude()));
                lon.setText(Double.toString(lc.getLongitude()));
                getloc.setText("Get Location");
                saveloc.setText("Edit Location");
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                md.enabled = b;
                dr.setValue(md);
            }
        });

        return view;
    }

}
