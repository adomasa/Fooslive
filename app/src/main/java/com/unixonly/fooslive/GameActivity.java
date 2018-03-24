package com.unixonly.fooslive;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.unixonly.fooslive.databinding.ActivityGameBinding;
import com.unixonly.fooslive.utils.PositionManager;

// TODO port activity
public class GameActivity extends AppCompatActivity implements PositionManager.OnPositionManagerInteractionListener {
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

    /**
     * TODO: move to separate UI utils class
     * Update image visibility
     * @param image ImageView instance reference
     * @param toVisible visible if true, invisible if false
     */
    private void updateVisibility(ImageView image, boolean toVisible) {
        if (toVisible) image.setVisibility(View.VISIBLE);
        else image.setVisibility(View.INVISIBLE);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    /**
     * Update guidelines images visibility based on passed flags
     * @param flagSet visibility flags container
     */
    @Override
    public void onPositionManagerCallback(int flagSet) {
        updateVisibility(mBinding.imageArrowTop,
                (flagSet | PositionManager.EXCEEDS_TOP) == flagSet);
        updateVisibility(mBinding.imageArrowBot,
                (flagSet | PositionManager.EXCEEDS_BOT) == flagSet);
        updateVisibility(mBinding.imageArrowLeft,
                (flagSet | PositionManager.EXCEEDS_LEFT) == flagSet);
        updateVisibility(mBinding.imageArrowRight,
                (flagSet | PositionManager.EXCEEDS_RIGHT) == flagSet);
    }
}
