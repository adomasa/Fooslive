package com.unixonly.fooslive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.unixonly.fooslive.utils.ConfigManager;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ConfigManager.load(this, R.raw.config);
            // Initialise high CPU consuming code here
            startActivity(new Intent(SplashActivity.this, MenuActivity.class));
        } catch (IOException e) {
            Log.e(TAG, "Couldn't load configuration file. Terminating application.");
        } finally {
            finish();
        }
    }
}
