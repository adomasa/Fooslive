package com.unixonly.fooslive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise high CPU consuming code here


        startActivity(new Intent(SplashActivity.this, MenuActivity.class));
        finish();
    }
}
