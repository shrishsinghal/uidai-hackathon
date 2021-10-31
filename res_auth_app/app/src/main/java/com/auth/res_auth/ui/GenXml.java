package com.auth.res_auth.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.auth.res_auth.NavActivity;
import com.auth.res_auth.R;


public class GenXml extends Fragment {




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_gen_xml, container, false);
        ((NavActivity) getActivity()).getSupportActionBar().setTitle("Patients in my city");












        return root;
    }

}
