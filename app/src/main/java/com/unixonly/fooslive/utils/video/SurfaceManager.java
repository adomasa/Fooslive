package com.unixonly.fooslive.utils.video;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.unixonly.fooslive.game.GameController;
import com.unixonly.fooslive.game.tracking.ColorDetector;
import com.unixonly.fooslive.game.tracking.ObjectDetector;
import com.unixonly.fooslive.utils.ConfigManager;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * This class is responsible for processing given
 *  frames and detecting the ball
 */
public class SurfaceManager implements TextureView.SurfaceTextureListener {
    private static String TAG = "SurfaceManager";

    private Context mContext;

    private CameraHandler mCameraHandler;
    private boolean mIsCameraMode;

    private VideoPlayer mVideoPlayer;

    private TextureView mTextureView;

    private SurfaceHolder mSurfaceHolder;

    private boolean mHSVSelected;
    private ObjectDetector mObjectDetector;

    public SurfaceManager(Context context,
                          TextureView textureView,
                          SurfaceHolder surfaceHolder,
                          boolean isCameraMode,
                          GameController gameController) {
        mContext = context;
        mTextureView = textureView;
        mSurfaceHolder = surfaceHolder;

        mObjectDetector = new ObjectDetector(
                new ColorDetector(),
                gameController
        );

        mIsCameraMode = isCameraMode;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int w, int h) {
        // Calculate the upscaling multipliers
        int calc_x_size = ConfigManager.getInt("width_process");
        int calc_y_size = ConfigManager.getInt("height_process");

        mObjectDetector.setUpscalingMultipliers(new PointF(
                w / (float)calc_x_size,
                h / (float)calc_y_size
        ));

        if (mIsCameraMode) {
            mCameraHandler = new CameraHandler(mContext, mTextureView);
        } else {
            mVideoPlayer = new VideoPlayer(mContext, surface);
        }
    }

    public void onScreenTouch(float x, float y) {
        Mat imageToProcess = new Mat();
        Mat hsvImage = new Mat();

        // Convert RGB to HSV colorspace
        Utils.bitmapToMat(mTextureView.getBitmap(), imageToProcess);
        Imgproc.cvtColor(imageToProcess, hsvImage, Imgproc.COLOR_RGB2HSV_FULL);

        double[] values = new double[3];

        // Extract the hsv value
        hsvImage.get((int)x, (int)y, values);

        Scalar hsvToSet = new Scalar(
                values[0], // Hue channel
                values[1], // Saturation channel
                values[2] // Value channel
        );

        mObjectDetector.setHsvColor(hsvToSet);
    }

    public void lockSelectedHSVValue() {
        mHSVSelected = true;
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
        // The table is currently drawn only if an Hsv value is selected
        if (!mHSVSelected) return;

        Canvas canvas = mSurfaceHolder.lockCanvas();

        if (!mObjectDetector.detect(canvas, mTextureView.getBitmap())) {
            // Remove all drawings
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
}
