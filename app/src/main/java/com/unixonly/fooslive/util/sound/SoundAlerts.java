package com.unixonly.fooslive.util.sound;

import android.content.Context;

import com.unixonly.fooslive.enums.EAlert;

/**
 * Created by paulius on 2/6/18.
 */

public class SoundAlerts {
    private Context mContext;

    private SoundPlayer mTeam1GoalPlayer;
    private SoundPlayer mTeam1WinPlayer;

    private SoundPlayer mTeam2GoalPlayer;
    private SoundPlayer mTeam2WinPlayer;

    public SoundAlerts(Context context) {
        mContext = context;
    }

    public void play(EAlert alertType) {
        switch (alertType) {
            case TEAM_1_GOAL: {
                mTeam1GoalPlayer.play();
                break;
            }
            case TEAM_1_WIN: {
                mTeam1WinPlayer.play();
                break;
            }
            case TEAM_2_GOAL: {
                mTeam2GoalPlayer.play();
                break;
            }
            case TEAM_2_WIN: {
                mTeam2WinPlayer.play();
                break;
            }
        }
    }

    public void setTeam1GoalSound(int resId) {
        mTeam1GoalPlayer = new SoundPlayer(mContext, resId);
    }

    public void setTeam1WinSound(int resId) {
        mTeam1WinPlayer = new SoundPlayer(mContext, resId);
    }

    public void setTeam2GoalSound(int resId) {
        mTeam2GoalPlayer = new SoundPlayer(mContext, resId);
    }

    public void setTeam2WinPlayer(int resId) {
        mTeam2WinPlayer = new SoundPlayer(mContext, resId);
    }
}
