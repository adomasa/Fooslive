package com.unixonly.fooslive.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class CustomVibrator {
    private final Vibrator mVibrator;
    private boolean mVibrating = false;
    private static int vibrationRepeatIndex;
    private static long[] vibrationPattern;

    public CustomVibrator(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Start vibration
     */
    public void start()
    {
        if (mVibrating) return;
        // >=26 Android SDK use waveforms to define vibration
        if (Build.VERSION.SDK_INT >= 26) {
            mVibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern,
                    vibrationRepeatIndex));
        }
        else mVibrator.vibrate(vibrationPattern, 0);
        mVibrating = true;
    }

    /**
     * Stop vibration
     */
    public void stop()
    {
        if (!mVibrating) return;
        mVibrator.cancel();
        mVibrating = false;
    }

}
