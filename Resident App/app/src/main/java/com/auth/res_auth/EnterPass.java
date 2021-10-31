package com.auth.res_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mukesh.OtpView;

public class EnterPass extends AppCompatActivity {

    OtpView enterPassText;
    Button verifyPass;
    String pass, passEntered;
    int clickcount;
    int allowedCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pass);

        enterPassText = findViewById(R.id.enterPassText);
        verifyPass = findViewById(R.id.btnVerifyPass);
        clickcount = 0;
        allowedCount = 6;


        verifyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterPassText.getText().toString().trim().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Password is mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }else if(enterPassText.getText().toString().trim().length()!=6) {

                    Toast.makeText(getApplicationContext(), "Please fill the details", Toast.LENGTH_SHORT).show();
                    return;

                }
                enterPassText.setEnabled(false);
                passEntered = enterPassText.getText().toString().trim();

                SharedPreferences shf = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
                pass = shf.getString("pass", null);

                if(passEntered.equals(pass)){
                    Intent navIntent = new Intent(EnterPass.this, NavActivity.class);
                    startActivity(navIntent);
                    finish();
                }else{
                    clickcount = clickcount+1;
                    allowedCount = allowedCount-clickcount;
                    String allowedC = String.valueOf(allowedCount);

                    if(allowedCount==0){
                        Toast.makeText(getApplicationContext(), "Incorrect password. All data has been deleted.", Toast.LENGTH_SHORT).show();
                        getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit().clear().commit();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(EnterPass.this, LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        }, 2000 );

                    }else{
                        Toast.makeText(getApplicationContext(), "Incorrect password. "+allowedC+" attempts remaining.", Toast.LENGTH_SHORT).show();
                        enterPassText.setEnabled(true);
                        return;
                    }




                }


            }
        });


    }
}