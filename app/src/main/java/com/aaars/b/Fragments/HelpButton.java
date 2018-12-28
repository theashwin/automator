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


public class HelpButton extends Fragment {

    TextView tv, tvhead;
    Switch toggle;
    EditText input, inputbeta, inputgamma;
    Button savePhone, savePhonebeta, savePhonegamma;
    String USER_ID;

    public Module md;
    DatabaseReference dr;


    public HelpButton() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_button, container, false);

        USER_ID = ModuleDesign.USER_ID;
        md = new Module();


        tvhead = view.findViewById(R.id.tvhead);
        tv = view.findViewById(R.id.tv);
        toggle = view.findViewById(R.id.toggle);
        input = view.findViewById(R.id.input);
        inputbeta = view.findViewById(R.id.inputbeta);
        inputgamma = view.findViewById(R.id.inputgamma);
        savePhone = view.findViewById(R.id.savePhone);
        savePhonebeta = view.findViewById(R.id.savePhonebeta);
        savePhonegamma = view.findViewById(R.id.savePhonegamma);

        tvhead.setText("Send SMS to your trusted contacts on a click");
        tv.setText("Send SMS with your location to your trusted contacts on clicking a sticky notification when in danger");

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("10");
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
                    if(md.parameters.get(1).length() == 10) {
                        inputbeta.setText(md.parameters.get(1));
                        inputbeta.setEnabled(false);
                        savePhonebeta.setText("Edit Number");
                    }
                    if(md.parameters.get(2).length() == 10) {
                        inputgamma.setText(md.parameters.get(2));
                        inputgamma.setEnabled(false);
                        savePhonegamma.setText("Edit Number");
                    }

                }
                else {
                    //INSTANTIATION OF MODULE 01
                    md.triggerid = 101;
                    md.activityid = 101;
                    md.enabled = true;
                    md.parameters.add("");
                    md.parameters.add("");
                    md.parameters.add("");
                    dr.setValue(md);

                    savePhone.setText("Save Number");
                    savePhonebeta.setText("Save Number");
                    savePhonegamma.setText("Save Number");
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

        savePhonebeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputbeta.isEnabled()) {
                    String phonebeta = inputbeta.getText().toString();
                    if (phonebeta.length() == 10) {
                        md.parameters.set(1,phonebeta);
                        dr.setValue(md);
                        inputbeta.setEnabled(false);
                        savePhonebeta.setText("Edit Number");
                    }
                }
                else {
                    inputbeta.setEnabled(true);
                    savePhonebeta.setText("Save Number");
                }
            }
        });

        savePhonegamma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputgamma.isEnabled()) {
                    String phonegamma = inputgamma.getText().toString();
                    if (phonegamma.length() == 10) {
                        md.parameters.set(2,phonegamma);
                        dr.setValue(md);
                        inputgamma.setEnabled(false);
                        savePhonegamma.setText("Edit Number");
                    }
                }
                else {
                    inputgamma.setEnabled(true);
                    savePhonegamma.setText("Save Number");
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
