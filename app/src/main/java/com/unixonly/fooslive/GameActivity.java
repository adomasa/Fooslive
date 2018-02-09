package com.unixonly.fooslive;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.unixonly.fooslive.databinding.ActivityGameBinding;

//TODO port activity
public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    //TODO flag constants for guidelines

    private ActivityGameBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (GameMode == ECaptureMode.Live)
//            _positionManager.StopListening();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (GameMode == ECaptureMode.Live)
//            _positionManager.StartListening();
    }
}
