package com.aaars.b;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Onboarding extends AppCompatActivity {

    //GOOGLE
    private static final String TAG = "Home";
    private static int RC_SIGN_IN = 9001;
    private static GoogleSignInClient mGoogleSignInClient;

    //PERMISSIONS
    static String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NFC,
            Manifest.permission.NFC_TRANSACTION_EVENT,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    // ONBOARDING DECLARATIONS
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    ImageButton mNextBtn;
    static Button mSkipBtn, mFinishBtn, mDo;
    static Intent rootIntent;
    static ImageView img;

    ImageView zero, one, two;
    ImageView[] indicators;

    int page = 0;

    //GOOGLE LOGIN FUNCTIONS
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
            finish();
        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    private  void updateUI(GoogleSignInAccount a) {

    }

    private void googleSignIn() {
        rootIntent = new Intent(this, Root.class);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            rootIntent = new Intent(Onboarding.this, Root.class);
            rootIntent.putExtra("EMAIL", account.getEmail());
            rootIntent.putExtra("NAME", account.getDisplayName());
            rootIntent.putExtra("USER_ID", account.getId());
            startActivity(rootIntent);
            updateUI(account);
            finish();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboarding);

        googleSignIn();

        //ONBOARDING DEFINITIONS
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNextBtn = (ImageButton) findViewById(R.id.intro_btn_next);
        mSkipBtn = (Button) findViewById(R.id.intro_btn_skip);
        mFinishBtn = (Button) findViewById(R.id.intro_btn_finish);
        mDo = (Button) findViewById(R.id.intro_btn_do);



        zero = (ImageView) findViewById(R.id.intro_indicator_0);
        one = (ImageView) findViewById(R.id.intro_indicator_1);
        two = (ImageView) findViewById(R.id.intro_indicator_2);

        indicators = new ImageView[]{zero, one, two};

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = ContextCompat.getColor(this, R.color.cyan);
        final int color2 = ContextCompat.getColor(this, R.color.orange);
        final int color3 = ContextCompat.getColor(this, R.color.green);
        final int[] colorList = new int[]{color1, color2, color3};

        final ArgbEvaluator evaluator = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //COLOR UPDATE ON SCROLL
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 2 ? position : position + 1]);
                mViewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        break;
                }
                updateIndicators(position);

                mNextBtn.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 2 ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //REQUIRED FOR SCROLL ACTIVITY
            }
        });

        //ONCLICK LISTENERS

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reIntent = new Intent(getApplicationContext(), Onboarding.class);
                startActivity(reIntent);
                finish();
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reIntent = new Intent(getApplicationContext(), Onboarding.class);
                startActivity(reIntent);
                finish();
            }
        });
    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_onboarding, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //FRAGMENT PLACEHOLDER
    public static class PlaceholderFragment extends Fragment {

        String[] btnC = new String[] {"Google Sign In", "Give Permissions", "Get Started!"};
        int[] imgC = new int[] {R.drawable.login, R.drawable.laptop, R.drawable.project};
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            String[] str = new String[]{getContext().getString(R.string.section_one),getContext().getString(R.string.section_two),getContext().getString(R.string.section_three)};
            textView.setText(str[getArguments().getInt(ARG_SECTION_NUMBER)-1]);

            img = rootView.findViewById(R.id.img);
            img.setImageResource(imgC[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            mDo = rootView.findViewById(R.id.intro_btn_do);
            mDo.setText(btnC[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            mDo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDoOnClick(getArguments().getInt(ARG_SECTION_NUMBER) - 1);
                }
            });
            return rootView;
        }

        private void mDoOnClick(int pos) {
            switch(pos) {
                case 0:
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent,RC_SIGN_IN);
                    break;
                case 1:
                    requestPermissions(permissions, 9999);
                    break;
                case 2:
                    Intent reIntent = new Intent(getContext(), Onboarding.class);
                    startActivity(reIntent);
                    getActivity().finish();
                    break;
            }
        }
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Login / Signup";
                case 1:
                    return "Permissions";
                case 2:
                    return "Blast Off";
            }
            return null;
        }
    }
}
