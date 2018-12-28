package com.aaars.b.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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


public class LostPhone extends Fragment {

    TextView tv, tvhead;
    Switch toggle;
    EditText input;
    Button email;
    String USER_ID;

    public Module md;
    DatabaseReference dr;


    public LostPhone() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volume_min, container, false);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        USER_ID = ModuleDesign.USER_ID;
        md = new Module();

        tvhead = view.findViewById(R.id.tvhead);
        tv = view.findViewById(R.id.tv);
        toggle = view.findViewById(R.id.toggle);
        input = view.findViewById(R.id.input);
        email = view.findViewById(R.id.savePhone);
        TextView paraphrase = view.findViewById(R.id.paraphrase);

        tvhead.setText("Take extreme measures to find the lost device");
        tv.setText("On receiving a sms containing keywords and paraphrase switches on the wifi, send location to the number and sounds alarm for recovering the device");
        paraphrase.setText("lostphone <paraphrase>");

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID).child("modules").child("6");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    md = dataSnapshot.getValue(Module.class);

                    if(!md.parameters.get(0).equals(""))
                        input.setEnabled(false);

                    toggle.setChecked(md.parameters.get(5).equals("true"));
                    input.setText(md.parameters.get(0));
                    email.setText("Edit Paraphrase");

                }
                else {
                    //INSTANTIATION OF MODULE 01
                    md.triggerid = 105;
                    md.activityid = 108;
                    md.enabled = true;
                    md.parameters.add("");
                    for(int i = 0; i < 6; i++)
                        md.parameters.add("false");
                    dr.setValue(md);

                    input.setEnabled(true);
                    email.setText("Save Paraphrase");
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
                    String paraphrase = input.getText().toString();
                    if (paraphrase.length() >= 3) {
                        md.parameters.set(0,paraphrase);
                        dr.setValue(md);
                        input.setEnabled(false);
                        email.setText("Edit Paraphrase");
                    }
                }
                else {
                    input.setEnabled(true);
                    email.setText("Save Paraphrase");
                }
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                md.parameters.set(5,(b?"true":"false"));
                dr.setValue(md);
            }
        });

        return view;
    }

}
