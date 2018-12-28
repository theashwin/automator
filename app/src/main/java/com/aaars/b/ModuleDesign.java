package com.aaars.b;

import android.content.Intent;
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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaars.b.Fragments.GeoFencing;
import com.aaars.b.Fragments.GetLocation;
import com.aaars.b.Fragments.HelpButton;
import com.aaars.b.Fragments.LocationWiFi;
import com.aaars.b.Fragments.LostPhone;
import com.aaars.b.Fragments.LowBatterySMS;
import com.aaars.b.Fragments.ModuleDesignFragment;
import com.aaars.b.Fragments.QuotesEmail;
import com.aaars.b.Fragments.RSS;
import com.aaars.b.Fragments.SMSWiFiOff;
import com.aaars.b.Fragments.SMSWiFiOn;
import com.aaars.b.Fragments.VolumeMax;
import com.aaars.b.Fragments.VolumeMin;
import com.aaars.b.Fragments.WiFiOnTimer;
import com.aaars.b.Fragments.WiFiTimer;

import com.aaars.b.Fragments.XKCD;
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


public class ModuleDesign extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button logout;
    FirebaseAuth mAuth;
    GoogleSignInAccount user;
    private GoogleSignInClient mGoogleSignInClient;

    public static String USER_ID;
    public static int MODULE_ID;
    private String TAG = "TAG";

    FrameLayout mFrame;
    BottomNavigationView mNav;
    ActionBarDrawerToggle toggle;

    private ModuleDesignFragment moduleDesignFragment;
    private LowBatterySMS lowBatterySMS;
    private WiFiTimer wiFiTimer;
    private WiFiOnTimer wiFiOnTimer;
    private GeoFencing geoFencing;
    private QuotesEmail quotesEmail;
    private SMSWiFiOn smsWiFiOn;
    private SMSWiFiOff smsWiFiOff;
    private VolumeMax volumeMax;
    private VolumeMin volumeMin;
    private HelpButton helpButton;
    private XKCD xkcd;
    private LostPhone lostPhone;
    private LocationWiFi locationWiFi;
    private RSS rss;
    private GetLocation getLocation;

    public UserData userData, intentData;
    DatabaseReference dr;

    private Intent i;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_discover:
                i.putExtra("fromChild", true);
                i.putExtra("USER_ID",USER_ID);
                i.putExtra("pos",R.id.nav_discover);
                startActivity(i);
                break;
            case R.id.nav_modules:
                i.putExtra("fromChild", true);
                i.putExtra("USER_ID",USER_ID);
                i.putExtra("pos",R.id.nav_modules);
                startActivity(i);
                break;
            case R.id.nav_profile:
                i.putExtra("fromChild", true);
                i.putExtra("USER_ID",USER_ID);
                i.putExtra("pos",R.id.nav_profile);
                startActivity(i);
                break;
            case R.id.help:
                break;
            case R.id.about:
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.restart:
                Intent i = new Intent(getApplicationContext(),Splash.class);
                startActivity(i);
                break;
            case R.id.exit:
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancelAll();
                finish();
                System.exit(0);
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
        i = new Intent(this, Root.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        final Intent intent = getIntent();
        intentData = new UserData();
        USER_ID = intent.getStringExtra("USER_ID");
        MODULE_ID = intent.getIntExtra("MODULE_ID", -1);


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


        //**********************************************//

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

        mFrame = (FrameLayout) findViewById(R.id.frame);
        mNav = (BottomNavigationView) findViewById(R.id.navigation);

        switch (MODULE_ID) {
            case 1:
                lowBatterySMS = new LowBatterySMS();
                setFragment(lowBatterySMS);
                break;
            case 2:
                wiFiTimer = new WiFiTimer();
                setFragment(wiFiTimer);
                break;
            case 3:
                wiFiOnTimer = new WiFiOnTimer();
                setFragment(wiFiOnTimer);
                break;
            case 4:
                geoFencing = new GeoFencing();
                setFragment(geoFencing);
                break;
            case 5:
                quotesEmail = new QuotesEmail();
                setFragment(quotesEmail);
                break;
            case 6:
                smsWiFiOn = new SMSWiFiOn();
                setFragment(smsWiFiOn);
                break;
            case 7:
                smsWiFiOff = new SMSWiFiOff();
                setFragment(smsWiFiOff);
                break;
            case 8:
                volumeMax = new VolumeMax();
                setFragment(volumeMax);
                break;
            case 9:
                volumeMin = new VolumeMin();
                setFragment(volumeMin);
                break;
            case 10:
                helpButton = new HelpButton();
                setFragment(helpButton);
                break;
            case 11:
                xkcd = new XKCD();
                setFragment(xkcd);
                break;
            case 15:
                lostPhone = new LostPhone();
                setFragment(lostPhone);
                break;
            case 13:
                rss = new RSS();
                setFragment(rss);
                break;
            case 14:
                locationWiFi = new LocationWiFi();
                setFragment(locationWiFi);
                break;
            case 12:
                getLocation = new GetLocation();
                setFragment(getLocation);
                break;
            default:
                moduleDesignFragment = new ModuleDesignFragment();
                setFragment(moduleDesignFragment);
                break;

        }

        mNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                i.putExtra("fromChild", true);
                i.putExtra("USER_ID",USER_ID);
                switch(item.getItemId()) {
                    case R.id.nav_modules:
                        i.putExtra("pos",R.id.nav_modules);
                        startActivity(i);
                        return true;
                    case R.id.nav_discover:
                        i.putExtra("pos",R.id.nav_discover);
                        startActivity(i);
                        return true;
                    case R.id.nav_profile:
                        i.putExtra("pos",R.id.nav_profile);
                        startActivity(i);
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

    //[UPDATE UI]
    public void updateUI(UserData userData){
        NavigationView mNavView = findViewById(R.id.nav_view);
        TextView username = mNavView.getHeaderView(0).findViewById(R.id.username);
        TextView useremail = mNavView.getHeaderView(0).findViewById(R.id.useremail);
        username.setText(userData.name);
        useremail.setText(userData.email);
    }

    //[LOGOUT]
    public void logout() {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent homeIntent = new Intent(ModuleDesign.this, Onboarding.class);
                        startActivity(homeIntent);
                        Toast.makeText(ModuleDesign.this,"Logged Out!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
