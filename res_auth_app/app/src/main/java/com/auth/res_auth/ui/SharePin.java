package com.auth.res_auth.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.auth.res_auth.NavActivity;
import com.auth.res_auth.R;


public class SharePin extends Fragment {

    String pinEncoded, randString;

    ImageView pinQr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_share_pin, container, false);
        ((NavActivity) getActivity()).getSupportActionBar().setTitle("Share Pin");


        pinQr = root.findViewById(R.id.pinQrImageview);


        SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getString(R.string.sharedpref), Context.MODE_PRIVATE);
        pinEncoded = prefs.getString("pinQR", "Pin not found");



        byte[] imageAsBytes = Base64.decode(pinEncoded.getBytes(), Base64.DEFAULT);
        pinQr.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        //Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();

        return root;
    }

}
