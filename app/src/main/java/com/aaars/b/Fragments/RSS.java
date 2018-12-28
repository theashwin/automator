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


public class RSS extends Fragment {

    TextView tv, tvhead;
    Switch toggle;
    EditText input, rss;
    Button email;
    String USER_ID;

    public Module md;
    DatabaseReference dr;


    public RSS() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rss, container, false);

        USER_ID = ModuleDesign.USER_ID;
        md = new Module();

        tvhead = view.findViewById(R.id.tvhead);
        tv = view.findViewById(R.id.tv);
        toggle = view.findViewById(R.id.toggle);
        input = view.findViewById(R.id.input);
        rss = view.findViewById(R.id.rss);
        email = view.findViewById(R.id.savePhone);

        tvhead.setText("Sends RSS feed bundle to you email address");
        tv.setText("Creates an RSS bundle specially for you to include all of your daily reads");

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("8");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md = dataSnapshot.getValue(Module.class);
                    toggle.setChecked(md.enabled);
                    if(md.parameters.get(0).contains("@") && md.parameters.get(0).contains(".")) {
                        input.setText(md.parameters.get(0));
                        input.setEnabled(false);
                        email.setText("Edit Email");
                    }
                    if(!md.parameters.get(1).equals("")) {
                        rss.setText(md.parameters.get(1));
                        rss.setEnabled(false);
                    }

                }
                else {
                    //INSTANTIATION OF MODULE 01
                    md.triggerid = 101;
                    md.activityid = 101;
                    md.enabled = true;
                    md.parameters.add("");
                    md.parameters.add("");
                    dr.setValue(md);

                    input.setEnabled(true);
                    rss.setEnabled(true);
                    email.setText("Save Email");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr.addValueEventListener(postListener);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.isEnabled()) {
                    String emailid = input.getText().toString();
                    if (emailid.contains("@") && emailid.contains(".") && emailid.length() > 10) {
                        md.parameters.set(0,emailid);
                        dr.setValue(md);
                        input.setEnabled(false);
                        rss.setEnabled(false);
                        email.setText("Edit Email");
                    }
                }
                else {
                    input.setEnabled(true);
                    rss.setEnabled(true);
                    email.setText("Save Email");
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
