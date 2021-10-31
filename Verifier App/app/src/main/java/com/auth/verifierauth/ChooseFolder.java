package com.auth.verifierauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class ChooseFolder extends AppCompatActivity {

    private static final int REQUEST_DIRECTORY = 123;
    Button uploadFold, btnScanQR;

    ImageView foldImageView;
    TextView foldNameText;
    String folderPath;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_folder);


        uploadFold = findViewById(R.id.btnUploadFold);
        btnScanQR = findViewById(R.id.btnScanQR);
        foldImageView = findViewById(R.id.foldImageView);
        foldNameText = findViewById(R.id.foldNameText);

        foldImageView.setVisibility(View.INVISIBLE);
        foldNameText.setVisibility(View.INVISIBLE);
        btnScanQR.setVisibility(View.INVISIBLE);

        uploadFold.setText("Choose folder");

        checkSharedPref();


        uploadFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ChooseFolder.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChooseFolder.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                }if(ContextCompat.checkSelfPermission(ChooseFolder.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ChooseFolder.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
                }


                final Intent chooserIntent = new Intent(ChooseFolder.this, DirectoryChooserActivity.class);

                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .newDirectoryName("DirChooserSample")
                        .allowReadOnlyDirectory(true)
                        .allowNewDirectoryNameModification(true)
                        .build();

                chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

                // REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
                startActivityForResult(chooserIntent, REQUEST_DIRECTORY);

            }
        });

        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextIntent = new Intent(ChooseFolder.this, ScanningQr.class);
                nextIntent.putExtra("fold", folderPath);
                startActivity(nextIntent);

            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                handleDirectoryChoice(data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            } else {
                // Nothing selected
            }
        }
    }

    private void handleDirectoryChoice(String fold) {

        uploadFold.setText("change folder");
        foldImageView.setVisibility(View.VISIBLE);
        foldNameText.setVisibility(View.VISIBLE);
        btnScanQR.setVisibility(View.VISIBLE);

        String foldName = fold.substring(fold.lastIndexOf("/")+1);

        foldNameText.setText(foldName);
        folderPath = fold;

        editor = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit();
        editor.putString("folderPath", folderPath);
        editor.apply();



    }

    private void checkSharedPref() {
        SharedPreferences shf = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
        String foldName = shf.getString("folderPath", null);

        if(foldName == null) {
            // do some thing
            uploadFold.setText("change folder");
            foldImageView.setVisibility(View.VISIBLE);
            foldNameText.setVisibility(View.VISIBLE);
            btnScanQR.setVisibility(View.VISIBLE);

            String foldNameDis = foldName.substring(foldName.lastIndexOf("/")+1);

            foldNameText.setText(foldNameDis);



        }
    }


}