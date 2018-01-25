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
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;

import com.unixonly.fooslive.Util.SizeComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by paulius on 1/24/18.
 *
 * Some code is taken from:
 * https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/com/example/android/camera2basic/Camera2BasicFragment.java
 */

public class CameraSetup {
    private static final int STATE_PREVIEW = 0;

    /***
     * The following 4 constants are for capturing a still image
     * */
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    private int displayWidth;
    private int displayHeight;

    private CameraManager cameraManager;
    private CameraDevice camera;
    private ImageReader imageReader;
    private CameraCaptureSession captureSession;

    private int currentState = STATE_PREVIEW;

    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;

    private Semaphore openCloseCameraLock;
    private SurfaceTexture drawingTexture;

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            openCloseCameraLock.release();
            camera = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            openCloseCameraLock.release();
            cameraDevice.close();
            camera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            openCloseCameraLock.release();
            cameraDevice.close();
            camera = null;
            /***
             * #TODO
             * Figure out what happens to the activity on camera error
             */
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback
            = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (currentState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
            }
        }
    };

    public CameraSetup(SurfaceTexture texture, int displayWidth, int displayHeight) {
        System.out.println(texture);
        this.drawingTexture = texture;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.openCloseCameraLock = new Semaphore(1);
        this.imageReader = ImageReader.newInstance(displayWidth, displayHeight, ImageFormat.JPEG, 1);
    }
    public void init(Context context) {
        this.cameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        chooseCamera(cameraManager);
    }
    private void chooseCamera(CameraManager manager) {
        String[] cameraIds = null;
        try {
            cameraIds = manager.getCameraIdList();

            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // Check if its back facing
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT)
                    continue;

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

                Size toSet = null;
                List<Size> biggerThanTexture = new ArrayList<>();
                List<Size> smallerThanTexture = new ArrayList<>();
                for (Size size : sizes) {
                    if (size.getWidth() <= this.displayWidth && size.getHeight() <= this.displayHeight) {
                        biggerThanTexture.add(size);
                    } else {
                        smallerThanTexture.add(size);
                    }
                }

                if (biggerThanTexture.size() > 0) {
                    toSet = Collections.min(biggerThanTexture, new SizeComparator());
                } else {
                    toSet = Collections.max(smallerThanTexture, new SizeComparator());
                }

                manager.openCamera(cameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {
            /***
             * #TODO
             * Handle camera access exception
             */
        }
    }
    private void createCameraPreviewSession() {
        try {
            // We configure the size of default buffer to be the size of camera preview we want.
            this.drawingTexture.setDefaultBufferSize(displayWidth, displayHeight);

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(drawingTexture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder
                    = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            camera.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == camera) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            captureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Finally, we start displaying the camera preview.
                                previewRequest = previewRequestBuilder.build();
                                captureSession.setRepeatingRequest(previewRequest,
                                        captureCallback, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            /***
                             * #TODO
                             * Handle configuration here
                             */
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            /***
             * #TODO
             * Handle camera exception here
             */
        }
    }

    public Image grabImage() {
        return this.imageReader.acquireLatestImage();
    }
}
