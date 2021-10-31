package com.auth.res_auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sumon.eagleeye.EagleEye;
import org.sumon.eagleeye.OnChangeConnectivityListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenerateXml extends AppCompatActivity {

    OtpView mPin;
    Button mGenXml;
    String pin;
    String TAG = "TAG";
    SharedPreferences.Editor editor;
    OtpView mOtpview;

    String uid, vid, txn_id, otp, eKycXML, fileName, randString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_xml);

        uid = null;
        vid = null;

        uid = getIntent().getStringExtra("aadhaar_num");
        //vid = getIntent().getStringExtra("vid_num");
        txn_id = getIntent().getStringExtra("new_txnId");
        //otp = getIntent().getStringExtra("otp");


        mPin = findViewById(R.id.pinEdittext);
        mOtpview = findViewById(R.id.otpEdittext);
        mGenXml = findViewById(R.id.genXmlBtn);

        checkNet();



        mGenXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(mOtpview.getText().toString().trim().isEmpty() || mPin.getText().toString().trim().isEmpty()){

                    Toast.makeText(getApplicationContext(), "OTP and PIN are mandatory", Toast.LENGTH_SHORT).show();

                    return;
                }else if(mOtpview.getText().toString().trim().length()!=6 || mPin.getText().toString().trim().length()!=4){

                    Toast.makeText(getApplicationContext(), "Please fill the details", Toast.LENGTH_SHORT).show();

                    return;
                }



                mOtpview.setEnabled(false);
                mPin.setEnabled(false);

                mGenXml.setEnabled(false);

                pin = mPin.getText().toString().trim();
                otp = mOtpview.getText().toString().trim();

                Log.d(TAG, "test6 aadhaar number is" + uid);

                String postUrl = "https://stage1.uidai.gov.in/eAadhaarService/api/downloadOfflineEkyc";
                RequestQueue requestQueue = Volley.newRequestQueue(GenerateXml.this);



                JSONObject postData = new JSONObject();
                try {
                    postData.put("uid", uid);
                    postData.put("vid", vid);
                    postData.put("txnNumber", txn_id);
                    postData.put("shareCode", pin);
                    postData.put("otp", otp);
                    Log.d(TAG, "test7 " + postData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "test8 "+ response);

                        try {
                            String status = response.getString("status").toLowerCase();


                            Log.d(TAG, "test9 "+ eKycXML);
                            if(status.equals("success")){
                                eKycXML = response.getString("eKycXML");
                                fileName = response.getString("fileName");
                                ekycDone();


                                //Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();



                            }else{
                                Toast.makeText(getApplicationContext(), "Incorrect OTP. Please start again.", Toast.LENGTH_SHORT).show();

                                Intent backIntent = new Intent(GenerateXml.this, LoginActivity.class);
                                startActivity(backIntent);
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

/*                        Toast.makeText(getApplicationContext(), "Error. Please start again.", Toast.LENGTH_SHORT).show();

                        Intent backIntent = new Intent(GenerateXml.this, LoginActivity.class);
                        startActivity(backIntent);
                        finish();*/
                    }
                }){
                    /** Passing some request headers* */
                    @Override
                    public Map<String, String> getHeaders(){
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");

                        return params;
                    }
                };

                requestQueue.add(jsonObjectRequest);





            }
        });



    }

    private void ekycDone() {

        Toast.makeText(getApplicationContext(), "E-KYC Generated", Toast.LENGTH_SHORT).show();

        randString = randomStringGenerator();

        editor = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit();
        editor.putString("uid", uid);
        editor.putString("vid", vid);
        editor.putString("pin",pin);
        editor.putString("eKycXML", eKycXML);
        editor.putString("fileName", fileName);
        editor.putString("randString", randString);
        editor.apply();


        JSONObject pinJson = new JSONObject();

        try {
            pinJson.put("pin", pin);
            pinJson.put("randString", randString);
            pinJson.put("fileName", fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

            generateQR(pinJson.toString(), "pinQR");


        storetoZipandOpen(eKycXML, randString);

        checkSharedPref();

    }


    private void checkSharedPref() {
        SharedPreferences shf = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
        String strPref = shf.getString("passProtection", null);

        if(strPref== null){
            Intent passIntent = new Intent(GenerateXml.this, PasswordActivity.class);
            startActivity(passIntent);
            finish();
        }
        else if(strPref.equals("y")) {
            Intent navIntent = new Intent(GenerateXml.this, NavActivity.class);
            startActivity(navIntent);
            finish();

        }
    }



    private void generateQR(String contentString, String spName ) {



        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(contentString, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            Log.d(TAG, "test11 "+ spName+" "+ bmp);

            saveQR(bmp, spName);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    private void saveQR(Bitmap bm, String name) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);


        editor = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit();
        editor.putString(name, encoded);
        editor.apply();
        Log.d(TAG, "test13 "+ name+" "+encoded);


    }


    public String randomStringGenerator() {
        String uuid = UUID.randomUUID().toString();
        String uuid1 = uuid.replaceAll("-", "");
        return uuid1;
    }

    public void storetoZipandOpen(String base, String fn) {



        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

        Log.d(TAG,"test "+root);

        File myDir = new File(root + "/MYAADHAAR/ekycZip");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }



        String fname = fn + ".zip";
        File file = new File(myDir, fname);
        Log.d(TAG,"test "+fname);


        if (file.exists())
            file.delete();
        try {

            FileOutputStream out = new FileOutputStream(file);
            byte[] pdfAsBytes = Base64.decode(base, 0);
            out.write(pdfAsBytes);
            out.flush();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/MYAADHAAR/ekycZip");
        File imgFile = new File(dir, fname);


        Intent sendIntent = new Intent(Intent.ACTION_VIEW);

        Uri uri;
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(file);
        } else {
            uri = Uri.parse("file://" + imgFile); // My work-around for new SDKs, causes ActivityNotFoundException in API 10.
        }

        editor = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).edit();
        editor.putString("ekycPath", imgFile.toString());
        editor.apply();


        Log.d(TAG,"test "+ uri.toString());


    }
    private void checkNet() {
        EagleEye.getStatus(this, new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d("qwer", "onChanged status: " + status);
                /*                Toast.makeText(LoginActivity.this, "" + status, Toast.LENGTH_SHORT).show();*/

                if(!status){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GenerateXml.this);
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