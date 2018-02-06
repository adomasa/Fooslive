package com.unixonly.fooslive.Camera;

import android.content.Context;
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
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by paulius on 1/24/18.
 *
 * Some code is taken from:
 * https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/com/example/android/camera2basic/Camera2BasicFragment.java
 */

public class CameraSetup {
    private static final String TAG = "Fooslive.CameraSetup";
    private static final int STATE_PREVIEW = 0;

    /***
     * The following 4 constants are for capturing a still image
     * */
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    private int mDisplayWidth;
    private int mDisplayHeight;

    private CameraManager mCameraManager;
    private CameraDevice mCamera;
    private ImageReader mImageReader;

    private int currentState = STATE_PREVIEW;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private CameraCaptureSession mCaptureSession;

    private Semaphore mOpenCloseCameraLock;
    private TextureView mDrawingTexture;

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the mCamera is opened.  We start mCamera preview here.
            mOpenCloseCameraLock.release();
            mCamera = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mOpenCloseCameraLock.release();
            cameraDevice.close();
            mCamera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mOpenCloseCameraLock.release();
            cameraDevice.close();
            mCamera = null;
            /***
             * #TODO
             * Figure out what happens to the activity on mCamera error
             */
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback
            = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (currentState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the mCamera preview is working normally.
                    break;
                }
            }
        }
    };

    public CameraSetup(TextureView texture, int mDisplayWidth, int mDisplayHeight) {
        mDrawingTexture = texture;
        mDisplayWidth = mDisplayWidth;
        mDisplayHeight = mDisplayHeight;
        mOpenCloseCameraLock = new Semaphore(1);
        mImageReader = ImageReader.newInstance(mDisplayWidth, mDisplayHeight, ImageFormat.JPEG, 1);
    }
    public void init(Context context) {
        mCameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        chooseCamera(mCameraManager);
    }
    private void chooseCamera(CameraManager manager) {
        String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();

            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // Check if its back facing
                if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT)
                    continue;

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

                // TODO: Check if app has permission to access camera
                manager.openCamera(cameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error occurred while accessing the camera!", e);
        }
    }
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mDrawingTexture.getSurfaceTexture();
            texture.setDefaultBufferSize(mDisplayWidth, mDisplayHeight);

            Surface surface = new Surface(texture);

            mPreviewRequestBuilder
                    = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            mCamera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The mCamera is already closed
                            if (null == mCamera) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for mCamera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Finally, we start displaying the mCamera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        captureCallback, null);
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "Error occurred while accessing the camera!", e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            Log.e(TAG, "Error occured while configuring the camera!");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error occurred while accessing the camera!", e);
        }
    }
}
