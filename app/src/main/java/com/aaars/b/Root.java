package com.aaars.b;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaars.b.Fragments.AboutFragment;
import com.aaars.b.Fragments.HelpFragment;
import com.aaars.b.UserData;

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

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class Root extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button logout;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    GoogleSignInAccount user;
    private GoogleSignInClient mGoogleSignInClient;

    public static String USER_ID;
    private String TAG = "TAG";
    private boolean fromChild = false;

    FrameLayout mFrame;
    BottomNavigationView mNav;
    ActionBarDrawerToggle toggle;

    private DiscoverFragment discoverFragment;
    private UserFragment userFragment;
    private ModuleFragment moduleFragment;
    private HelpFragment helpFragment;
    private AboutFragment aboutFragment;

    public UserData userData, intentData;
    DatabaseReference dr, dr2;
    public LastRun lr;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_discover:
                setFragment(discoverFragment);
                break;
            case R.id.nav_modules:
                setFragment(moduleFragment);
                break;
            case R.id.nav_profile:
                setFragment(userFragment);
                break;
            case R.id.help:
                setFragment(helpFragment);
                break;
            case R.id.about:
                setFragment(aboutFragment);
                break;
            case R.id.restart:
                Intent i = new Intent(getApplicationContext(),Splash.class);
                startActivity(i);
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.exit:
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancelAll();
                finish();
                break;
            default:
                return false;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        call();
    }

    void call() {
        Intent i = new Intent(Root.this, Triggers.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        final Intent intent = getIntent();
        intentData = new UserData();
        lr = new LastRun();
        USER_ID = intent.getStringExtra("USER_ID");
        intentData.email = intent.getStringExtra("EMAIL");
        intentData.name = intent.getStringExtra("NAME");
        intentData.photo = intent.getStringExtra("PHOTO");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            USER_ID = account.getId();
        }

        if(USER_ID == null) {
            Intent i = new Intent(this, Onboarding.class);
            startActivity(i);
        }

        //FIREBASE DATABASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        dr = database.getInstance().getReference().child("users").child(USER_ID);

        ValueEventListener postListener = new ValueEventListener() {
        @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userData = dataSnapshot.getValue(UserData.class);
                    updateUI(userData);
                } else {
                    FirebaseDatabase.getInstance().getReference("users").child(USER_ID).setValue(intentData);
                    userData = intentData;
                    updateUI(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        dr.addValueEventListener(postListener);

        dr2 = database.getInstance().getReference().child("users").child(USER_ID).child("lastrun");
        ValueEventListener lastr = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    lr = dataSnapshot.getValue(LastRun.class);
                }
                else {
                    dr2.setValue(lr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dr2.addValueEventListener(lastr);

        //*************************************//

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView mNavView = findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

        TextView username = mNavView.getHeaderView(0).findViewById(R.id.username);
        TextView useremail = mNavView.getHeaderView(0).findViewById(R.id.useremail);
        username.setText(intentData.name);
        useremail.setText(intentData.email);

        mFrame = (FrameLayout) findViewById(R.id.frame);
        mNav = (BottomNavigationView) findViewById(R.id.navigation);

        discoverFragment = new DiscoverFragment();
        moduleFragment = new ModuleFragment();
        userFragment = new UserFragment();
        helpFragment = new HelpFragment();
        aboutFragment = new AboutFragment();

        setFragment(discoverFragment);
        mNav.setSelectedItemId(R.id.nav_discover);

        fromChild = intent.getBooleanExtra("fromChild", false);
        if(fromChild) {
            int pos = intent.getIntExtra("pos",1);
            switch(pos) {
                case R.id.nav_modules:
                    setFragment(moduleFragment);
                    break;
                case R.id.nav_discover:
                    setFragment(discoverFragment);
                    break;
                case R.id.nav_profile:
                    setFragment(userFragment);
                    break;
            }
        } else {
            setFragment(discoverFragment);
            mNav.setSelectedItemId(R.id.nav_discover);
        }

        mNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                switch(item.getItemId()) {
                    case R.id.nav_modules:
                        setFragment(moduleFragment);
                        return true;
                    case R.id.nav_discover:
                        setFragment(discoverFragment);
                        return true;
                    case R.id.nav_profile:
                        setFragment(userFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updateUI(UserData userData){
        NavigationView mNavView = findViewById(R.id.nav_view);

        TextView username = mNavView.getHeaderView(0).findViewById(R.id.username);
        TextView useremail = mNavView.getHeaderView(0).findViewById(R.id.useremail);
        username.setText(userData.name);
        useremail.setText(userData.email);
    }

    public void logout() {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Root.this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(Root.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent homeIntent = new Intent(Root.this, Onboarding.class);
                        startActivity(homeIntent);
                        Toast.makeText(Root.this,"Logged Out!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
