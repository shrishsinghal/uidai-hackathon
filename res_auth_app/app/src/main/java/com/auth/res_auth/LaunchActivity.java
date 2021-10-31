package com.auth.res_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class LaunchActivity extends AppCompatActivity {

    private  static int SPLASH_SCREEN = 3000;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);


        SharedPreferences shf = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
        String strPref = shf.getString("uid", null);

        if(strPref != null) {
            // do some thing
            intent = new Intent(getApplicationContext(), EnterPass.class);

        }else{
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN );


    }
}