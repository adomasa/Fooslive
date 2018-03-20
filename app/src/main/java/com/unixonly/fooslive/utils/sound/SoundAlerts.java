package com.unixonly.fooslive.utils.sound;

import android.content.Context;

import com.unixonly.fooslive.game.model.Alert;

import java.io.IOException;

public class SoundAlerts {
    private Context mContext;

    private SoundPlayer mTeam1GoalPlayer;
    private SoundPlayer mTeam1WinPlayer;

    private SoundPlayer mTeam2GoalPlayer;
    private SoundPlayer mTeam2WinPlayer;

    public SoundAlerts(Context context) {
        mContext = context;
    }

    public void play(@Alert.Type int alertType) {
        switch (alertType) {
            case Alert.TEAM_1_GOAL: {
                mTeam1GoalPlayer.play();
                break;
            }
            case Alert.TEAM_1_WIN: {
                mTeam1WinPlayer.play();
                break;
            }
            case Alert.TEAM_2_GOAL: {
                mTeam2GoalPlayer.play();
                break;
            }
            case Alert.TEAM_2_WIN: {
                mTeam2WinPlayer.play();
                break;
            }
        }
    }

    public void setTeam1GoalSound(int resId) throws IOException {
        mTeam1GoalPlayer = new SoundPlayer(mContext, resId);
    }

    public void setTeam1WinSound(int resId) throws IOException  {
        mTeam1WinPlayer = new SoundPlayer(mContext, resId);
    }

    public void setTeam2GoalSound(int resId) throws IOException  {
        mTeam2GoalPlayer = new SoundPlayer(mContext, resId);
    }

    public void setTeam2WinPlayer(int resId) throws IOException  {
        mTeam2WinPlayer = new SoundPlayer(mContext, resId);
    }
}
