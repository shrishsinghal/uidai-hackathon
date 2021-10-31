package com.auth.res_auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.sumon.eagleeye.EagleEye;
import org.sumon.eagleeye.OnChangeConnectivityListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class LoginActivity extends AppCompatActivity {

    EditText aadhaarEdit, captchaEdit;
    String aadhaarnum, txn_id, vidnum, captchaVal;
    Button sendOTP;
    String TAG = "TAG";
    String uid, vid;

    ImageView mcaptchaImageview;

    String captchaBase64String, captchaTxnId;

    ImageButton reloadCaptcha;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkNet();


        aadhaarEdit = findViewById(R.id.aadhaarEdittext);
        captchaEdit = findViewById(R.id.captchaEdittext);
        sendOTP = findViewById(R.id.sendOTPbtn);
        mcaptchaImageview = findViewById(R.id.captchaImageview);
        reloadCaptcha = findViewById(R.id.btnreloadCaptcha);

        if( getIntent().getExtras() != null)
        {
            uid = getIntent().getStringExtra("aadhaar_num");
            vid = getIntent().getStringExtra("vid_num");
            aadhaarEdit.setText(uid);
        }


        getCaptcha();


        reloadCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaptcha();
            }
        });


        aadhaarEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aadhaarEdit.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {
                aadhaarEdit.setError(null);
            }
        });

        captchaEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                captchaEdit.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {
                captchaEdit.setError(null);
            }
        });







        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(aadhaarEdit.getText().toString().trim().isEmpty() || captchaEdit.getText().toString().trim().isEmpty()){

                    if(aadhaarEdit.getText().toString().trim().isEmpty() ){
                        aadhaarEdit.setError("Mandatory");
                    }
                    if(captchaEdit.getText().toString().trim().isEmpty()){
                        captchaEdit.setError("Mandatory");
                    }

                    return;
                }else if(aadhaarEdit.getText().toString().trim().length()!=12 || captchaEdit.getText().toString().trim().length()!=6){
                    if(aadhaarEdit.getText().toString().trim().length()!=12){
                        aadhaarEdit.setError("Invalid");
                    }
                    if(captchaEdit.getText().toString().trim().length()!=6){
                        captchaEdit.setError("Invalid");
                    }
                    return;
                }

                aadhaarEdit.setEnabled(false);
                captchaEdit.setEnabled(false);
                sendOTP.setEnabled(false);

                aadhaarnum = aadhaarEdit.getText().toString().trim();
                captchaVal = captchaEdit.getText().toString().trim();

                txn_id = "MYAADHAAR:59142477-3f57-465d-8b9a-75b28fe48725";

                Log.d(TAG, "test0 aadhaar number is" + aadhaarnum);

                String postUrl = "https://stage1.uidai.gov.in/unifiedAppAuthService/api/v2/generate/aadhaar/otp";
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

                JSONObject postData = new JSONObject();
                try {
                    postData.put("uidNumber", aadhaarnum);
                    postData.put("captchaTxnId", captchaTxnId);
                    postData.put("captchaValue", captchaVal);
                    postData.put("transactionId", txn_id);
                    Log.d(TAG, "test1" + postData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "test2 "+ response);

                        try {


                            String value = response.getString("status").toLowerCase();



                            if(value.equals("success")){
                                Intent otpIntent = new Intent(LoginActivity.this, GenerateXml.class);
                                otpIntent.putExtra("txnId", txn_id);
                                otpIntent.putExtra("aadhaar_num", aadhaarnum);
                                String new_txnId = response.getString("txnId");
                                otpIntent.putExtra("new_txnId",new_txnId);
                                Log.d(TAG, "test3 "+ new_txnId);

                                startActivity(otpIntent);
                                finish();
                                Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(getApplicationContext(), "Incorrect Captcha, Please try again", Toast.LENGTH_SHORT).show();

                                Intent reIntent = new Intent(LoginActivity.this, LoginActivity.class);
                                startActivity(reIntent);
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

                        Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();

/*                        Intent reIntent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(reIntent);
                        finish();*/


                    }



                })
                {
                    /** Passing some request headers* */
                    @Override
                    public Map<String, String> getHeaders(){
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("appid", "MYAADHAAR");
                        params.put("Accept-Language", "en_in");
                        params.put("x-request-id", UUID.randomUUID().toString());


                        return params;
                    }
                };




                requestQueue.add(jsonObjectRequest);


            }
        });






    }

    private void getCaptcha() {

        Log.d(TAG, "test11");


        String postUrl = "https://stage1.uidai.gov.in/unifiedAppAuthService/api/v2/get/captcha";
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("langCode", "en");
            postData.put("captchaLength", "3");
            postData.put("captchaType", "2");
            Log.d(TAG, "test1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "test12 "+ postData);



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "test2 "+ response);


                try {

                    captchaTxnId = response.getString("captchaTxnId");
                    captchaBase64String = response.getString("captchaBase64String");

                    Log.d(TAG, "test13 "+ captchaBase64String);

                    byte[] imageAsBytes = Base64.decode(captchaBase64String.getBytes(), Base64.DEFAULT);
                    mcaptchaImageview.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                    Log.d(TAG, "test14 "+ imageAsBytes);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })

        {
            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders(){
            Map<String, String>  params = new HashMap<String, String>();
            params.put("Content-Type", "application/json");


            return params;
        }
        };

        requestQueue.add(jsonObjectRequest);

        Log.d(TAG, "test15 ");


    }


    private void checkNet() {
        EagleEye.getStatus(this, new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d("qwer", "onChanged status: " + status);
                /*                Toast.makeText(LoginActivity.this, "" + status, Toast.LENGTH_SHORT).show();*/

                if(!status){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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