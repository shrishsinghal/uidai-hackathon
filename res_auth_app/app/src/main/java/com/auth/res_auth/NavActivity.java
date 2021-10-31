package com.auth.res_auth;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.auth.res_auth.ui.SharePin;
import com.auth.res_auth.ui.ShareXml;
import com.google.android.material.navigation.NavigationView;

import org.sumon.eagleeye.EagleEye;
import org.sumon.eagleeye.OnChangeConnectivityListener;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;


    public static final String TAG = "TAG";



    private FrameLayout frameLayout;
    public static int frameLayoutIDN;
    private static int currentFragment;

    NavigationView navigationView;


    private static final int SHARE_XML = 0;
    private static final int SHARE_PIN = 1;
    private static final int GEN_XML = 2;

    String uid, vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        checkNet();
        checkSharedPref();


        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
        uid = prefs.getString("uid", null);//"No name defined" is the default value.
        vid = prefs.getString("vid", null); //0 is the default value.



        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener)this);
        View headerView = navigationView.getHeaderView(0);
        TextView aadhaarText = headerView.findViewById(R.id.aadhaarText);

        String lastFour = uid.substring(uid.length() - 4);
        aadhaarText.setText("XXXX XXXX "+lastFour);


        frameLayout = findViewById(R.id.main_framelayout);
        setFragment(new ShareXml(), SHARE_XML);

        frameLayoutIDN = frameLayout.getId();


    }

    private void checkSharedPref() {
        SharedPreferences shf = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
        String strPref = shf.getString("uid", null);

        if(strPref == null) {
            // do some thing

            Intent loginIntent = new Intent(NavActivity.this, LoginActivity.class);
            getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit().clear().commit();


            startActivity(loginIntent);
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(currentFragment==SHARE_XML){
            getMenuInflater().inflate(R.menu.nav, menu);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setFragment(Fragment fragment, int fragmentNo){
        currentFragment = fragmentNo;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    public boolean onNavigationItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id == R.id.nav_shareXml) {
            setFragment(new ShareXml(), SHARE_XML);

        }
        else if(id == R.id.nav_sharePin) {
            setFragment(new SharePin(), SHARE_PIN);

        }
/*        if(id == R.id.nav_genXml) {

            Intent loginIntent = new Intent(NavActivity.this, LoginActivity.class);
            loginIntent.putExtra("aadhaar_num", uid);
            loginIntent.putExtra("vid_num", vid);


            getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit().clear().commit();


            startActivity(loginIntent);
            finish();
            //Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();


        }*/


        else if(id == R.id.nav_logout) {


        showTheDialog();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    private void showTheDialog(){
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("This will delete all data including E-KYC and Security Pin. You will be required to generate a new E-KYC again.")
                .setPositiveButton("Logout", null)
                .setNegativeButton("Cancel", null)
                .show();

        dialog.setCancelable(false);

        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(NavActivity.this, LoginActivity.class);

                getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit().clear().commit();


                startActivity(loginIntent);
                finish();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                return;
            }
        });
    }




    private void checkNet() {
        EagleEye.getStatus(this, new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d("qwer", "onChanged status: " + status);
                /*                Toast.makeText(LoginActivity.this, "" + status, Toast.LENGTH_SHORT).show();*/

                if(!status){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NavActivity.this);
                    builder.setTitle("No Internet");
                    builder.setMessage("Please check your internet connection and try again.")
                            .setCancelable(false)
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    checkNet();

                                }
                            })
                            .show();
                }

            }
        });
    }

}