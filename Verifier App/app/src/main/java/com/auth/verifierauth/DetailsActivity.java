package com.auth.verifierauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class DetailsActivity extends AppCompatActivity {

    String pin, randString, filePath, newPath, newUnzipped, fileName;

    ImageView profileImage;
    TextView nameText, dobText, addressText, sexText;

    Button nextEntry;

    private static final String ACTION = "in.gov.uidai.rdservice.face.STATELESS_MATCH";
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    private static final int STATELESS_MATCH_REQ_CODE = 123;
    private static final int DOCUMENT_PICKER_REQ_CODE = 124;

    String pathForLogs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        pin = getIntent().getStringExtra("pin");
        randString = getIntent().getStringExtra("randString");
        filePath = getIntent().getStringExtra("pathFile");
        fileName = getIntent().getStringExtra("fileName");


        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        dobText = findViewById(R.id.dobText);
        addressText = findViewById(R.id.addressText);
        sexText = findViewById(R.id.sexText);
        nextEntry = findViewById(R.id.btnNextEntry);

        String temp = new File(filePath).getParent();
        pathForLogs = new File(temp).getParent();


        Log.d("TAG", "test fileName " + fileName);
        Log.d("TAG", "test pin " + pin);

        Uri uri = Uri.parse("file://" + filePath);
        newPath = copyFileToInternalStorage(uri, "zipped", randString+".zip");

        String path = new File(newPath).getParent();
        String path1 = new File(path).getParent();
        newUnzipped = path1+"/unzipped/";
        Log.d("TAG", "test newpath " + newPath);
        Log.d("TAG", "test newUnzipped " + newUnzipped);

        try {
            File src = new File(newPath);
            ZipFile zipFile = new ZipFile(src);
                if (zipFile.isEncrypted()) {
                zipFile.setPassword(pin);
            }
            String dest = new String(newUnzipped);
            zipFile.extractAll(dest);
        } catch (ZipException e) {
            e.printStackTrace();
        }



            File file = new File(newUnzipped+fileName);
        try {
            Log.d("TAG", "test file " + file.getPath());
            String xmlPath = file.getPath().replace(".zip", ".xml");
            Log.d("TAG", "test xmlPath " + xmlPath);

            InputStream is = new FileInputStream(xmlPath);
            XmlToJson xmlToJson = new XmlToJson.Builder(is, null).build();
            is.close();
            JSONObject jsonObject = xmlToJson.toJson();

            setData(jsonObject);

            Log.d("TAG", "test json " + jsonObject);


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }



        nextEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(ACTION);

                sendIntent.putExtra("request",REQUEST);
                sendIntent.putExtra("request",REQUEST);



                Intent intent = new Intent(getApplicationContext(), MethodActivity.class);
                startActivity(intent);
            }
        });



    }


    private void setData(JSONObject jsonObject) throws JSONException {


        JSONObject jsonApi = (JSONObject)jsonObject.get("OfflinePaperlessKyc");
        JSONObject jsonApi2 = (JSONObject)jsonApi.get("UidData");

        JSONObject jsonPOI = (JSONObject)jsonApi2.get("Poi");

        sexText.setText(jsonPOI.getString("gender"));
        nameText.setText(jsonPOI.getString("name"));
        dobText.setText(jsonPOI.getString("dob"));

        JSONObject jsonPOA = (JSONObject)jsonApi2.get("Poa");

        String address = jsonPOA.getString("house") +", "
                +jsonPOA.getString("street")+", "
                +jsonPOA.getString("loc")+", near "
                +jsonPOA.getString("landmark")+", "
                +jsonPOA.getString("vtc")+", "
                +jsonPOA.getString("subdist")+", district: "
                +jsonPOA.getString("dist")+", state: "
                +jsonPOA.getString("state")+", "
                +jsonPOA.getString("country");

        addressText.setText(address);



        String phtEncoded = jsonApi2.getString("Pht");
        byte[] imageAsBytes = Base64.decode(phtEncoded.getBytes(), Base64.DEFAULT);
        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        String log;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        String refId = jsonApi.getString("referenceId");
        String lastFour = refId.substring(0,4);
        String generationTimestamp = refId.substring(4);



        JSONObject json = new JSONObject();
        json.put("name",jsonPOI.getString("name") );
        json.put("randstring", randString);
        json.put("hashedEmail", jsonPOI.getString("e"));
        json.put("hashedMobile", jsonPOI.getString("m"));
        json.put("OfflinePaperlessKyc referenceId", jsonApi.getString("referenceId"));
        json.put("verifierTimestamp", currentDateandTime);
        json.put("ekycgenerationTimestamp", generationTimestamp);
        json.put("lastFour", lastFour);

        log = json.toString();
        Log.d("TAG", "test pathforlog " + pathForLogs);

        appendLog(log, pathForLogs);

    }



    public void appendLog(String text, String tempPath)
    {
        File logFile = new File(tempPath+"/logs.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private String copyFileToInternalStorage(Uri uri, String newDirName, String name) {
        Uri returnUri = uri;


        File output;
        if (!newDirName.equals("")) {
            File dir = new File(getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(getFilesDir() + "/" + newDirName + "/" + name);
        } else {
            output = new File(getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("TAG", "test 123");

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {

            Log.e("Exception", e.getMessage());
            Log.e("TAG", "test 123456");
        }
        Log.e("TAG", "test successful");
        Log.e("TAG", "test "+output.getPath());

        return output.getPath();
    }




}