package com.aaars.b;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aaars.b.Root.USER_ID;

public class UserFragment extends Fragment {
    private List<Notif> data;
    private RecyclerView rv;
    private UserData userData;
    DatabaseReference dr;
    DatabaseReference last;
    LastRun lr;

    public UserFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        lr = new LastRun();

        final RecyclerView rvuser = view.findViewById(R.id.rvuser);
        final TextView tvhead = view.findViewById(R.id.tvhead);
        final TextView tv = view.findViewById(R.id.tv);

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        dr = database.getInstance().getReference().child("users").child(USER_ID);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userData = dataSnapshot.getValue(UserData.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        };
        dr.addValueEventListener(postListener);



        //LASTRUN
        last = database.getInstance().getReference().child("users").child(USER_ID).child("lastrun");
        ValueEventListener lastr = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    lr = dataSnapshot.getValue(LastRun.class);
                    LinearLayoutManager llmuser = new LinearLayoutManager(getContext());
                    rvuser.setLayoutManager(llmuser);
                    rvuser.setHasFixedSize(true);
                    initializeData();
                    RVAdapter adapter = new RVAdapter(data);
                    rvuser.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        last.addValueEventListener(lastr);



        return view;
    }

    private void initializeData(){
        try {
            data = new ArrayList<>();
            data.add(new Notif("Device Security", "Set volume to min through SMS", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.red), 9));
            data.add(new Notif("Safety", "Send SMS with device location when battery is critically low", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.orange), 1));
            data.add(new Notif("Wifi", "Turn Wi-Fi OFF at specific time to save battery life", R.drawable.network, ContextCompat.getColor(getContext(), R.color.cardGreen), 2));
            data.add(new Notif("Device Security", "Turns OFF Wi-Fi through SMS", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.red), 7));
            data.add(new Notif("Wifi", "Turn Wi-Fi ON at specific time to connect to the Internet", R.drawable.network, ContextCompat.getColor(getContext(), R.color.cardGreen), 3));
            data.add(new Notif("Location", "Log time spent at specific location", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.yellove), 4));
            data.add(new Notif("Device Security", "Turns On Wi-Fi through SMS", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.red), 6));
            data.add(new Notif("Email", "Sends you Quote of the Day to your email address", R.drawable.mail, ContextCompat.getColor(getContext(), R.color.cyan), 5));
            data.add(new Notif("Safety", "Sends location to selected contacts on notification tap", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.orange), 10));
            data.add(new Notif("Device Security", "Recover lost device through SMS", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.red), 15));
            data.add(new Notif("Email", "Sends you a comic snippet to your email address", R.drawable.mail, ContextCompat.getColor(getContext(), R.color.cyan), 11));
            data.add(new Notif("Device Security", "Set volume to max through SMS", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.red), 8));
            data.add(new Notif("Email", "Sends bundled RSS feed to your email address", R.drawable.mail, ContextCompat.getColor(getContext(), R.color.cyan), 13));
            data.add(new Notif("Device Security", "Locate your phone through SMS", R.drawable.alpha, ContextCompat.getColor(getContext(), R.color.red), 12));
            data.add(new Notif("Wifi", "Turn Wi-Fi ON at specific location for seamless switch", R.drawable.network, ContextCompat.getColor(getContext(), R.color.cardGreen), 14));
        }
        catch(Exception e) {

        }
    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.DataViewHolder>{

        List<Notif> notif;

        RVAdapter(List<Notif> persons){
            this.notif = persons;
        }

        @Override
        public int getItemCount() {
            return notif.size();
        }

        @Override
        public RVAdapter.DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_user, viewGroup, false);
            RVAdapter.DataViewHolder pvh = new RVAdapter.DataViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RVAdapter.DataViewHolder dataViewHolder, int i) {
            try {
                dataViewHolder.desc.setText(notif.get(i).desc);
                dataViewHolder.img.setImageResource(notif.get(i).img);
                dataViewHolder.cv.setCardBackgroundColor(notif.get(i).clr);
                dataViewHolder.footer.setText((!lr.lastrun.get(notif.get(i).moduleid).equals("0") ? "LAST RAN : " + lr.lastrun.get(notif.get(i).moduleid).substring(0, 20) : "NEVER RAN"));
            }
            catch (Exception e) {}
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class DataViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView header;
            TextView desc;
            ImageView img;
            TextView footer;

            DataViewHolder(View itemView) {
                super(itemView);
                cv = itemView.findViewById(R.id.cvuser);
                desc = itemView.findViewById(R.id.desc);
                img = itemView.findViewById(R.id.img);
                footer = itemView.findViewById(R.id.footer);
            }
        }
    }
}

class Notif {
    String header;
    String desc;
    int img;
    int clr;
    int moduleid;

    Notif(String header, String desc, int img, int clr, int moduleid) {
        this.header = header;
        this.desc = desc;
        this.img = img;
        this.clr = clr;
        this.moduleid = moduleid;
    }
}
