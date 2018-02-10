package com.unixonly.fooslive;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.unixonly.fooslive.databinding.ActivityGameBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//TODO port activity
public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private static final int DISPLAY_TOP = 1;
    private static final int DISPLAY_BOT = 1 << 1;
    private static final int DISPLAY_LEFT = 1 << 2;
    private static final int DISPLAY_RIGHT = 1 << 3;

    @IntDef(flag=true, value={
            DISPLAY_TOP,
            DISPLAY_BOT,
            DISPLAY_LEFT,
            DISPLAY_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Guidelines {}

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

    /**
     * Update guidelines images visibility based on passed flags
     * @param flagSet visibility flags container
     */
    private void updateGuidelines(@Guidelines int flagSet) {
        updateVisibility(mBinding.imageArrowTop, (flagSet | DISPLAY_TOP) == flagSet);
        updateVisibility(mBinding.imageArrowBot, (flagSet | DISPLAY_BOT) == flagSet);
        updateVisibility(mBinding.imageArrowLeft, (flagSet | DISPLAY_LEFT) == flagSet);
        updateVisibility(mBinding.imageArrowRight, (flagSet | DISPLAY_RIGHT) == flagSet);
    }

    /**
     * Update image visibility
     * @param image ImageView instance reference
     * @param toVisible visible if true, invisible if false
     */
    private void updateVisibility(ImageView image, boolean toVisible) {
        if (toVisible)
            image.setVisibility(View.VISIBLE);
        else
            image.setVisibility(View.INVISIBLE);
    }
}
