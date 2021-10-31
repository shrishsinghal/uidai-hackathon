package com.auth.verifierauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MethodActivity extends AppCompatActivity {

    Button uploadfile, next;
    int PICKFILE_RESULT_CODE = 100;
    String src;
    private static final int MY_CAMERA_REQUEST_CODE = 200;

    ImageView fileImage;
    TextView filenameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);

        uploadfile = findViewById(R.id.btnUpload);
        next = findViewById(R.id.btnNExt);
        fileImage = findViewById(R.id.fileImageview);
        filenameText = findViewById(R.id.fileNameText);

        next.setVisibility(View.INVISIBLE);
        fileImage.setVisibility(View.INVISIBLE);
        filenameText.setVisibility(View.INVISIBLE);

        uploadfile.setText("Upload file");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        }
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextIntent = new Intent(MethodActivity.this, ScanQr.class);

                nextIntent.putExtra("pathFile", src);

                startActivity(nextIntent);

            }
        });



    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICKFILE_RESULT_CODE){
            Uri uri = data.getData();
            src = uri.getPath();
            String filename = src.substring(src.lastIndexOf("/")+1);
            //Toast.makeText(MethodActivity.this, "file path is "+ src, Toast.LENGTH_SHORT).show();

            uploadfile.setText("Change file");
            next.setVisibility(View.VISIBLE);
            fileImage.setVisibility(View.VISIBLE);
            filenameText.setVisibility(View.VISIBLE);
            filenameText.setText(filename);

        }

    }


}