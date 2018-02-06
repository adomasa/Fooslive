package com.unixonly.fooslive.gamecontrol;

import com.unixonly.fooslive.interfaces.OnTimerUpdateListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paulius on 2/6/18.
 */

public class GameTimer extends TimerTask {
    public static long sTime;

    private OnTimerUpdateListener mListener;
    private Timer mTimer;
    private long mToAdd;
    private boolean mStarted = false;
    private boolean mStop = false;

    public GameTimer(long interval) {
        mTimer = new Timer();
        mToAdd = interval;
    }

    public void start() {
        if (mStarted)
            return;

        mTimer.scheduleAtFixedRate(this, mToAdd, mToAdd);
        mStarted = true;
    }

    public void stop() {
        if (!mStarted)
            return;

        mStop = true;
    }

    @Override
    public void run() {
        sTime += mToAdd;

        mListener.onTimerUpdate();

        if (mStop)
        {
            mTimer.cancel();
            mStop = false;
            mStarted = false;
        }
    }

    public void setOnTimerUpdateListener(OnTimerUpdateListener listener) {
        mListener = listener;
    }
}
