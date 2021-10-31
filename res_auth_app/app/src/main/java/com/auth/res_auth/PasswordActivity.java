package com.auth.res_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mukesh.OtpView;

public class PasswordActivity extends AppCompatActivity {

    OtpView passText1, passText2;
    String pass;
    Button btnSetPass;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passText1 = findViewById(R.id.passText1);
        passText2 = findViewById(R.id.passText2);
        btnSetPass = findViewById(R.id.btnSetPass);


        btnSetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(passText1.getText().toString().trim().isEmpty() || passText2.getText().toString().trim().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Password is mandatory", Toast.LENGTH_SHORT).show();
                    return;

                }else if(passText1.getText().toString().trim().length()!=6 || passText2.getText().toString().trim().length()!=6){

                    Toast.makeText(getApplicationContext(), "Please fill the details", Toast.LENGTH_SHORT).show();
                    return;

                }else if(!passText1.getText().toString().trim().equals(passText2.getText().toString().trim())){

                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                passText1.setEnabled(false);
                passText2.setEnabled(false);


                pass = passText1.getText().toString().trim();

                editor = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit();
                editor.putString("pass", pass);
                editor.putString("passProtection", "y");
                editor.apply();


                Intent navIntent = new Intent(PasswordActivity.this, NavActivity.class);
                startActivity(navIntent);
                finish();


            }
        });




    }
}