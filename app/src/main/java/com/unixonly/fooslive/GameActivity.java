package com.unixonly.fooslive;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.unixonly.fooslive.databinding.ActivityGameBinding;
import com.unixonly.fooslive.game.GameController;
import com.unixonly.fooslive.model.Mode;
import com.unixonly.fooslive.guidelines.PhonePositionManager;
import com.unixonly.fooslive.video.SurfaceManager;

// TODO port activity
public class GameActivity extends AppCompatActivity implements
        PhonePositionManager.OnPositionManagerInteractionListener {
    private static final String TAG = "GameActivity";

    private ActivityGameBinding mBinding;

    private @Mode.Type int mode;

    private PhonePositionManager phonePositionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_menu);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game);

        // Determine game mode
        // If there is no video URI attached in intent, so it's live mode
        mode = getIntent().getData() == null ? Mode.LIVE : Mode.RECORD;

        // Initialise components
        try {
            phonePositionManager = new PhonePositionManager(this);
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, e.getMessage());
            //TODO: disable guidelines in case of exception
        }

        GameController gameController = new GameController();
        SurfaceManager surfaceManager = new SurfaceManager(this,
                mBinding.textureCamera, mBinding.surfaceBallTrace.getHolder(), gameController);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: check whether guidelines are enabled by user
        if (mode == Mode.LIVE) phonePositionManager.stopListening();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: check whether guidelines are enabled by user
        if (mode == Mode.LIVE) phonePositionManager.startListening();
    }

    private void updateVisibility(ImageView image, boolean isVisible) {
        image.setVisibility(isVisible ? View.VISIBLE : View.VISIBLE);
    }

    /**
     * Update guidelines images visibility based on passed flags
     * @param flagSet visibility flags container
     */
    @Override
    public void onPositionManagerCallback(int flagSet) {
        updateVisibility(mBinding.imageArrowTop,
                (flagSet | PhonePositionManager.EXCEEDS_TOP) == flagSet);
        updateVisibility(mBinding.imageArrowBot,
                (flagSet | PhonePositionManager.EXCEEDS_BOT) == flagSet);
        updateVisibility(mBinding.imageArrowLeft,
                (flagSet | PhonePositionManager.EXCEEDS_LEFT) == flagSet);
        updateVisibility(mBinding.imageArrowRight,
                (flagSet | PhonePositionManager.EXCEEDS_RIGHT) == flagSet);
    }
}
