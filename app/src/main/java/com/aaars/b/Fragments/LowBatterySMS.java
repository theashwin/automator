package com.aaars.b.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class LowBatterySMS extends Fragment {

    TextView tv, tvhead;
    Switch toggle;
    EditText input;
    Button savePhone;
    String USER_ID;

    public Module md;
    DatabaseReference dr;


    public LowBatterySMS() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_low_battery_sm, container, false);

        USER_ID = ModuleDesign.USER_ID;
        md = new Module();

        tvhead = view.findViewById(R.id.tvhead);
        tv = view.findViewById(R.id.tv);
        toggle = view.findViewById(R.id.toggle);
        input = view.findViewById(R.id.input);
        savePhone = view.findViewById(R.id.savePhone);

        tvhead.setText("Send SMS to your trusted contacts when battery is low.");
        tv.setText("Sending SMS to your trusted contacts can be of great help when your battery runs out of power");

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("1");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md = dataSnapshot.getValue(Module.class);
                    toggle.setChecked(md.enabled);
                    if(md.parameters.get(0).length() == 10) {
                        input.setText(md.parameters.get(0));
                        input.setEnabled(false);
                        savePhone.setText("Edit Number");
                    }

                }
                else {
                    //INSTANTIATION OF MODULE 01
                    md.triggerid = 101;
                    md.activityid = 101;
                    md.enabled = true;
                    md.parameters.add("");
                    dr.setValue(md);

                    input.setEnabled(true);
                    savePhone.setText("Save Number");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr.addValueEventListener(postListener);

        savePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.isEnabled()) {
                    String phone = input.getText().toString();
                    if (phone.length() == 10) {
                        md.parameters.set(0,phone);
                        dr.setValue(md);
                        input.setEnabled(false);
                        savePhone.setText("Edit Number");
                    }
                }
                else {
                    input.setEnabled(true);
                    savePhone.setText("Save Number");
                }
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
