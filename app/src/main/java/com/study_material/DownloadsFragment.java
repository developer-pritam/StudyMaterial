package com.study_material;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

public class DownloadsFragment extends Fragment {


    private static final String TAG = "SMSD";
    View view;
    boolean first = true;
    public DownloadsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String path =  Environment.getExternalStorageDirectory().toString() + "/" +Environment.DIRECTORY_DOWNLOADS  ;


        Log.d(TAG, "Path: " + path);

        try {
            File directory = new File(path);
            File[] files = directory.listFiles();



        try {

            Log.d(TAG, "Size: "+ files.length);

            for (int i = 0; i < files.length; i++)
            {
                Log.d(TAG, "FileName:" + files[i].getName());
            }
        }catch ( Exception e){
            Log.e(TAG, e.getMessage(), e);
        }


        }
catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
}
        if (first) {
            view = inflater.inflate(R.layout.fragment_downloads, container, false);

        }
        return view;
    }
}