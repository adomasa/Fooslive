package com.unixonly.fooslive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.unixonly.fooslive.utils.PropertiesManager;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise high CPU consuming code here
        PropertiesManager.load(this);
        startActivity(new Intent(SplashActivity.this, MenuActivity.class));
        finish();
    }
}
