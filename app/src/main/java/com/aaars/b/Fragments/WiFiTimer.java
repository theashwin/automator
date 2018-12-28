package com.aaars.b.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aaars.b.Module;
import com.aaars.b.ModuleDesign;
import com.aaars.b.R;
import com.aaars.b.Root;
import com.aaars.b.Splash;
import com.aaars.b.TriggerReceiver;
import com.aaars.b.Triggers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.ALARM_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;


public class WiFiTimer extends Fragment {

    TextView tv, tvhead;
    Switch toggle;
    Button click;
    String USER_ID;
    CheckBox mon, tue, wed, thu, fri, sat, sun;
    TimePicker timePicker;

    public Module md;
    DatabaseReference dr;

    private int hr, min;


    public WiFiTimer() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_timer, container, false);

        USER_ID = ModuleDesign.USER_ID;
        md = new Module();

        tvhead = view.findViewById(R.id.tvhead);
        tv = view.findViewById(R.id.tv);
        toggle = view.findViewById(R.id.toggle);
        click = view.findViewById(R.id.click);

        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);
        sat = view.findViewById(R.id.sat);
        sun = view.findViewById(R.id.sun);

        timePicker = view.findViewById(R.id.time);

        tvhead.setText("Turn Wi-Fi off at specific time");
        tv.setText("Wi-Fi can be turned off to save battery, during sleeping hours or during study time on selected days");

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("2");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md = dataSnapshot.getValue(Module.class);
                    toggle.setChecked(md.enabled);
                    mon.setChecked(md.parameters.get(0).equals("true"));
                    tue.setChecked(md.parameters.get(1).equals("true"));
                    wed.setChecked(md.parameters.get(2).equals("true"));
                    thu.setChecked(md.parameters.get(3).equals("true"));
                    fri.setChecked(md.parameters.get(4).equals("true"));
                    sat.setChecked(md.parameters.get(5).equals("true"));
                    sun.setChecked(md.parameters.get(6).equals("true"));
                    timePicker.setCurrentHour(Integer.parseInt(md.parameters.get(7)));
                    timePicker.setCurrentMinute(Integer.parseInt(md.parameters.get(8)));
                }
                else {
                    //INSTANTIATION OF MODULE 02
                    md.triggerid = 102;
                    md.activityid = 102;
                    md.enabled = false;
                    md.parameters.add("false");
                    md.parameters.add("false");
                    md.parameters.add("false");
                    md.parameters.add("false");
                    md.parameters.add("false");
                    md.parameters.add("false");
                    md.parameters.add("false");
                    md.parameters.add("0");
                    md.parameters.add("0");
                    dr.setValue(md);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr.addValueEventListener(postListener);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hr = timePicker.getCurrentHour();
                min = timePicker.getCurrentMinute();

                md.parameters.set(0,mon.isChecked()?"true":"false");
                md.parameters.set(1,tue.isChecked()?"true":"false");
                md.parameters.set(2,wed.isChecked()?"true":"false");
                md.parameters.set(3,thu.isChecked()?"true":"false");
                md.parameters.set(4,fri.isChecked()?"true":"false");
                md.parameters.set(5,sat.isChecked()?"true":"false");
                md.parameters.set(6,sun.isChecked()?"true":"false");

                md.parameters.set(7,Integer.toString(hr));
                md.parameters.set(8,Integer.toString(min));
                md.enabled = true;

                dr.setValue(md);

                Intent i = new Intent(getContext(),Splash.class);
                startActivity(i);
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
