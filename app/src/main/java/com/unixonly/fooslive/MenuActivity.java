package com.unixonly.fooslive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.osgi.OpenCVInterface;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (!OpenCVLoader.initDebug())
        {
            // Initialization error
            System.out.println("Error while loading native libraries!");
            System.exit(0);
        }
        // Check if OpenCV works by using a class from the API
        Mat mat = new Mat();
        mat = null;
    }
}
