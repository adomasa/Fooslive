package com.unixonly.fooslive.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PositionManager implements SensorEventListener {
    private static final String TAG = "PositionManager";

    // Live mode holding guidelines indication flags
    public static final int EXCEEDS_TOP = 1;
    public static final int EXCEEDS_BOT = 1 << 1;
    public static final int EXCEEDS_LEFT = 1 << 2;
    public static final int EXCEEDS_RIGHT = 1 << 3;

    @IntDef(flag = true, value = {
            EXCEEDS_TOP,
            EXCEEDS_BOT,
            EXCEEDS_LEFT,
            EXCEEDS_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Guideline {}

    private CustomVibrator mVibrator;
    private float mRoll;
    private float mPitch;
    private float mReferencePointRoll;
    private int mAccuracy;
    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private final int pitchOffset;
    private final int rollOffset;
    private final int suggestedPitchMin;
    private final int suggestedPitchMax;
    private final int maxRollDeviation;
    private boolean mGameStarted;

    public PositionManager(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mVibrator = new CustomVibrator(context);

        pitchOffset = ConfigManager.getInt("sensors.pitch.offset");
        rollOffset = ConfigManager.getInt("sensors.roll.offset");
        suggestedPitchMin = ConfigManager.getInt("sensors.pitch.min");
        suggestedPitchMax = ConfigManager.getInt("sensors.pitch.max");
        maxRollDeviation = ConfigManager.getInt("sensors.roll.max_deviation");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // There is only one type of sensor registered, so we can ignore sensor identifier
        if (mAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW
                || mAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w(TAG, "Low or unreliable rotation sensor accuracy");
            return;
        }

        float[] rotationMatrix = new float[9];
        float[] rotationVector = new float[event.values.length];

        // Extract raw data
        System.arraycopy(event.values, 0, rotationVector, 0, rotationVector.length);

        // Parse raw data
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        // Calibration
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
                SensorManager.AXIS_Y, adjustedRotationMatrix);

        //Retrieve calibrated data
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        mPitch = orientation[1] * -57;
        mRoll = orientation[2] * -57;

        processPosition();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // There is only one type of sensor registered, so we can ignore sensor identifier
        mAccuracy = accuracy;
    }

    /**
     * Analyse current phone position by creating a flag set which determines whether user needs to
     * change phone position for video analysis to work properly. Send flag set to class this
     * instance was created from using listener interface
     */
    private void processPosition()
    {
        @Guideline int flagSet = 0;

        if (mPitch > suggestedPitchMax - pitchOffset)
            flagSet |= EXCEEDS_TOP;
        else if (mPitch < suggestedPitchMin + pitchOffset)
            flagSet |= EXCEEDS_BOT;

        if (!mGameStarted)
        {
            ((OnPositionManagerInteractionListener)mContext).onPositionManagerCallback(flagSet);
            return;
        }

        if (mRoll < mReferencePointRoll - maxRollDeviation - rollOffset)
            flagSet |= EXCEEDS_LEFT;
        else if (mRoll > mReferencePointRoll + maxRollDeviation + rollOffset)
            flagSet |= EXCEEDS_RIGHT;

        ((OnPositionManagerInteractionListener)mContext).onPositionManagerCallback(flagSet);

        //Toggle vibration if necessary
        if (flagSet != 0)
            mVibrator.start();
        else
            mVibrator.stop();
    }

    public interface OnPositionManagerInteractionListener {
        void onPositionManagerCallback(@Guideline int flagSet);
    }

    public void startListening() {
        if (!mSensorManager.registerListener(
                this, mRotationSensor, SensorManager.SENSOR_DELAY_GAME))
            Log.e(TAG, "Vibration sensor unsupported or registered unsuccessfully");
    }

    public void stopListening() {
        mVibrator.stop();
        mSensorManager.unregisterListener(this);
    }

    public void onGameStarted() {
        mReferencePointRoll = mRoll;
        mGameStarted = true;
    }

}
