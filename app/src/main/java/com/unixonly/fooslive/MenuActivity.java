package com.unixonly.fooslive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Load binary files and enable hardware acceleration
        if (!OpenCVLoader.initDebug(true)) {
            // Error while loading binary files
            Log.w("Initialization error:", "Error while initializing!");
            System.exit(0);
        }
    }
}
