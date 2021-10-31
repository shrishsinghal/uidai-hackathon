package com.auth.verifierauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectMethod extends AppCompatActivity {

    Button btnChooseFold, btnChooseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_method);

        btnChooseFold = findViewById(R.id.btnChooseFold);
        btnChooseFile = findViewById(R.id.btnChooseFile);

        btnChooseFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMethod.this, ChooseFolder.class);
                startActivity(intent);
            }
        });

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMethod.this, MethodActivity.class);
                startActivity(intent);
            }
        });

    }
}