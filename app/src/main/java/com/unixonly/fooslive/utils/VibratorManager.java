package com.unixonly.fooslive.utils;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

//TODO: review in detail
public class VibratorManager {
    private final Vibrator mVibrator;
    private boolean mVibrating = false;
    private final int vibrationRepeatIndex = ConfigManager.getInt("vibration.ind_repeat");
    private final long[] vibrationPattern =
            ConfigManager.getLongList("vibration.pattern").stream().mapToLong(i->i).toArray();

    public VibratorManager(Vibrator vibrator) {
        mVibrator = vibrator;
    }

    /**
     * Start vibration
     */
    public void vibrate() {
        if (mVibrating) return;
        // >=26 Android SDK use waveforms to define vibration
        if (Build.VERSION.SDK_INT >= 26) {
            mVibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern,
                    vibrationRepeatIndex));
        } else mVibrator.vibrate(vibrationPattern, 0);

        mVibrating = true;
    }

    /**
     * Stop vibration
     */
    public void cancel() {
        if (!mVibrating) return;
        mVibrator.cancel();
        mVibrating = false;
    }

}
