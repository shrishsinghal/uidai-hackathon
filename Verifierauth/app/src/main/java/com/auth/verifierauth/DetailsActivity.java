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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class DetailsActivity extends AppCompatActivity {

    String pin, randString, filePath, newPath, newUnzipped, fileName;

    ImageView profileImage;
    TextView nameText, dobText, addressText, sexText;

    Button nextEntry;

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
                +jsonPOA.getString("loc")+", "
                +jsonPOA.getString("landmark")+", "
                +jsonPOA.getString("vtc")+", "
                +jsonPOA.getString("subdist")+", "
                +jsonPOA.getString("dist")+", "
                +jsonPOA.getString("state")+", "
                +jsonPOA.getString("country");

        addressText.setText(address);



        String phtEncoded = jsonApi2.getString("Pht");
        byte[] imageAsBytes = Base64.decode(phtEncoded.getBytes(), Base64.DEFAULT);
        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

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