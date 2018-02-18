package com.unixonly.fooslive.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.unixonly.fooslive.GameActivity;
import com.unixonly.fooslive.R;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

/** Some code is taken from:
 * https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/com/
 * example/android/camera2basic/Camera2BasicFragment.java
 */

public class CameraHandler {
    private static final String TAG = "CameraHandler";

    private static final String ERROR_CAMERA_ACCESS = "Error occurred while accessing the camera! ";
    private static final String LOG_HALT_ACTIVITY = "Terminating activity. ";

    private int mPreviewWidth;
    private int mPreviewHeight;

    private CameraDevice mCamera;
    private ImageReader mImageReader;
    private Context mContext;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private CameraCaptureSession mCaptureSession;

    private Semaphore mOpenCloseCameraLock;
    private TextureView mDrawingTexture;

    public CameraHandler(Context context, TextureView texture) {
        mContext = context;
        mDrawingTexture = texture;
        mPreviewWidth = PropertiesManager.getInt(context.getResources()
                .getString(R.string.key_width_preview));
        mPreviewHeight = PropertiesManager.getInt(context.getResources()
                .getString(R.string.key_height_preview));
        mOpenCloseCameraLock = new Semaphore(1);
        mImageReader = ImageReader.newInstance(mPreviewWidth, mPreviewHeight, ImageFormat.JPEG,
                1);
    }

    /**
     * Starts a camera preview session and starts a stream of frames
     *  to the given TextureView instance
     */
    public void start() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        chooseCamera(manager);
    }


    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mOpenCloseCameraLock.release();
            mCamera = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mOpenCloseCameraLock.release();
            cameraDevice.close();
            mCamera = null;

            Log.e(TAG, "Camera disconnected." + LOG_HALT_ACTIVITY);
            ((GameActivity)mContext).finish();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mOpenCloseCameraLock.release();
            cameraDevice.close();
            mCamera = null;

            Log.e(TAG, "Camera error: " + error + "." + LOG_HALT_ACTIVITY);
            ((GameActivity)mContext).finish();
        }

        private void createCameraPreviewSession() {
            try {
                SurfaceTexture texture = mDrawingTexture.getSurfaceTexture();
                texture.setDefaultBufferSize(mPreviewWidth, mPreviewHeight);

                Surface surface = new Surface(texture);

                mPreviewRequestBuilder
                        = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);

                mCamera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(
                                    @NonNull CameraCaptureSession cameraCaptureSession) {
                                // The mCamera is already closed
                                if (mCamera == null) return;

                                // When the session is ready, we start displaying the preview.
                                mCaptureSession = cameraCaptureSession;
                                try {
                                    // Auto focus should be continuous for mCamera preview.
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                    // Finally, we start displaying the mCamera preview.
                                    mPreviewRequest = mPreviewRequestBuilder.build();
                                    mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                            mCaptureCallback, null);
                                } catch (CameraAccessException e) {
                                    Log.e(TAG, ERROR_CAMERA_ACCESS + LOG_HALT_ACTIVITY, e);
                                    ((GameActivity)mContext).finish();
                                }
                            }

                            @Override
                            public void onConfigureFailed(
                                    @NonNull CameraCaptureSession cameraCaptureSession) {
                                Log.e(TAG, "Error occurred while configuring the camera!");
                                ((GameActivity)mContext).finish();
                            }
                        }, null
                );
            } catch (CameraAccessException e) {
                Log.e(TAG, ERROR_CAMERA_ACCESS + LOG_HALT_ACTIVITY + e);
                ((GameActivity)mContext).finish();
            }
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        //TODO: remove redundant code segment
        private void process(CaptureResult result) {
        }
    };

    private void chooseCamera(CameraManager manager) {
        String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();

            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // Check if its back facing
                if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) continue;

                StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
                Size optimalSize = chooseOptimalSize(sizes);

                mDrawingTexture.getSurfaceTexture().setDefaultBufferSize(
                        optimalSize.getWidth(),
                        optimalSize.getHeight()
                );

                // Permissions are checked before creating GameActivity, this condition should
                // never be true
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "App doesn't have permissions on GameActivity." +
                            LOG_HALT_ACTIVITY);
                    ((GameActivity)mContext).finish();
                    return;
                }

                manager.openCamera(cameraId, mStateCallback, null);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, ERROR_CAMERA_ACCESS + LOG_HALT_ACTIVITY, e);
            ((GameActivity)mContext).finish();
        }
    }

    /**
     * Chooses a camera size, which is closest to our preview size
     * @param sizes
     * A Size array, containing all possible camera sizes
     * @return
     * A class of type Size, which is closest to the preview resolution
     */
    private Size chooseOptimalSize(Size[] sizes) {
        int diffWidth = Math.abs(mPreviewWidth - sizes[0].getWidth());
        int diffHeight = Math.abs(mPreviewHeight - sizes[0].getHeight());
        int initialDiff = diffWidth + diffHeight;
        int which = 0;

        for (int i = 1; i < sizes.length; i++) {
            int resultWidth = Math.abs(mPreviewWidth - sizes[i].getWidth());
            int resultHeight = Math.abs(mPreviewHeight - sizes[i].getHeight());

            int result = resultWidth + resultHeight;

            if (result < initialDiff) {
                which = i;
                initialDiff = result;
            }
        }

        return sizes[which];
    }
}
