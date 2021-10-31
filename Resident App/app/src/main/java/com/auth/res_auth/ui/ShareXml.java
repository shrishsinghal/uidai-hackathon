package com.auth.res_auth.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.auth.res_auth.NavActivity;
import com.auth.res_auth.R;

import java.io.File;
import java.net.URLConnection;


public class ShareXml extends Fragment {

    String  randString, ekycPath;

    ImageView xmlQr;
    Button mBtnShare;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_share_xml, container, false);
        ((NavActivity) getActivity()).getSupportActionBar().setTitle("Share XML");

        mBtnShare = root.findViewById(R.id.btnShareEkyc);


        SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getString(R.string.sharedpref), Context.MODE_PRIVATE);
        randString = prefs.getString("randString", "randString not found");
        ekycPath = prefs.getString("ekycPath", "Ekyc not found");


        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(ekycPath);

                if(fileWithinMyDir.exists()) {
                    intentShareFile.setType("application/zip");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(ekycPath));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
*/
/*
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                intentShareFile.setType(URLConnection.guessContentTypeFromName("something.zip"));
                intentShareFile.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse(ekycPath));

                //if you need
                //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
                //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

                startActivity(Intent.createChooser(intentShareFile, "Share File"));


*/
// We assume the file we want to load is in the documents/ subdirectory
// of the internal storage

                //File documentsPath = new File(getActivity().getFilesDir(), "Documents");
                //File file = new File(documentsPath, "sample.pdf");
                File file = new File(ekycPath);
// This can also in one line of course:
// File file = new File(Context.getFilesDir(), "documents/sample.pdf");

                Uri uri = FileProvider.getUriForFile(getContext(), "com.mydomain.fileprovider", file);

                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("application/zip")
                        .setStream(uri)
                        .setChooserTitle("Choose bar")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);




            }
        });






        Log.d("test", "randString status: " + randString);

        return root;
    }

}
