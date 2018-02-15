package com.unixonly.fooslive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.unixonly.fooslive.utils.PropertiesManager;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise high CPU consuming code here
        try {
            PropertiesManager.load(this);
            startActivity(new Intent(SplashActivity.this, MenuActivity.class));
        } catch (IOException e) {
            Log.e(TAG, "Couldn't load configuration file. Terminating application.");
        } finally {
            finish();
        }
    }
}
