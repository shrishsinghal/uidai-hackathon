package com.auth.verifierauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;


import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQr extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    String filePath;
    String qrString;
    private ZXingScannerView mScannerView;
    JSONObject jsonObject;
    String randomString, pin, fileName;
    private static final int MY_CAMERA_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }


        filePath = getIntent().getStringExtra("pathFile");
        mScannerView = new ZXingScannerView(this);

        setContentView(mScannerView);
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
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        qrString = rawResult.getText();

        Log.d("TAG", "test " + qrString);


        try {
            jsonObject = new JSONObject(qrString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            pin = jsonObject.getString("pin");
            randomString = jsonObject.getString("randString");
            fileName = jsonObject.getString("fileName");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent detIntent = new Intent(ScanQr.this, DetailsActivity.class);

        detIntent.putExtra("pathFile", filePath);
        detIntent.putExtra("pin", pin);
        detIntent.putExtra("randString", randomString);
        detIntent.putExtra("fileName", fileName);


        //Toast.makeText(ScanQr.this, "pin is "+ pin, Toast.LENGTH_SHORT).show();

        startActivity(detIntent);


        finish();
    }
}