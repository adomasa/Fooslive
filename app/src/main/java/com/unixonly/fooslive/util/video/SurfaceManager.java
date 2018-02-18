package com.unixonly.fooslive.util.video;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

public class SurfaceManager implements TextureView.SurfaceTextureListener {
    private static String TAG = "SurfaceManager";
    // TODO
    /* private GameActivity mActivity */
    private SurfaceHolder mSurfaceHolder;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    public SurfaceManager(Context context, SurfaceHolder holder) {
        /* mActivity = (GameActivity)context; */
        mSurfaceHolder = holder;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int w, int h) {
        // TODO
        /*
        _activity.GameView.LayoutParameters = new FrameLayout.LayoutParams(w, h);

            // Set the upscaling constant
            _activity.SetMultipliers(w, h);

            SurfaceHolder.SetFixedSize(w, h);

            SurfaceTexture = surface;

            // Check if we use video mode
            if (_activity.GameMode == CaptureMode.RECORDING) {
                Surface = new Surface(surface);
                _activity.SetUpRecordMode(w, h);
            }
            else
            _activity.SetUpCameraMode();
         */
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        return true;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int w, int h) {
        Log.wtf(TAG, "Surface texture size changed. It shouldn't.");
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {
        // TODO
        /*
        // The table is currently drawn only if an Hsv value is selected
        if (!_activity.BallColorSelected)
            return;
        */

        Canvas canvas = mSurfaceHolder.lockCanvas();

        // TODO
        /*
        if (!_activity.detectBall(canvas))
        {
            // Remove all drawings
            canvas.DrawColor(Color.Transparent, PorterDuff.Mode.Clear);
        }
        */

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
}
