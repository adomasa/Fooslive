package com.unixonly.fooslive;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.unixonly.fooslive.databinding.ActivityGameBinding;

// TODO port activity
public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private ActivityGameBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_menu);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        TODO: if (GameMode == ECaptureMode.Live) _positionManager.StopListening();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//       TODO: if (GameMode == ECaptureMode.Live) _positionManager.StartListening();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
