package com.unixonly.fooslive.utils.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.unixonly.fooslive.game.model.Alert;

import java.io.IOException;

public class SoundAlerts {
    public static final String TAG = "SoundAlerts";
    private Context mContext;

    private MediaPlayer mTeam1GoalPlayer;
    private MediaPlayer mTeam1WinPlayer;

    private MediaPlayer mTeam2GoalPlayer;
    private MediaPlayer mTeam2WinPlayer;

    public SoundAlerts(Context context) {
        mContext = context;
    }

    public void play(@Alert.Type int alertType) {
        MediaPlayer targetPlayer;
        switch (alertType) {
            case Alert.TEAM_1_GOAL:
                targetPlayer = mTeam1GoalPlayer;
                break;
            case Alert.TEAM_1_WIN:
                targetPlayer = mTeam1WinPlayer;
                break;
            case Alert.TEAM_2_GOAL:
                targetPlayer = mTeam2GoalPlayer;
                break;
            case Alert.TEAM_2_WIN:
                targetPlayer = mTeam2WinPlayer;
                break;
            default:
                Log.e(TAG, "Unknown alertType on play()");
                return;
        }

        if (targetPlayer.isPlaying()) targetPlayer.stop();

        targetPlayer.start();
    }

    public void setTeam1GoalSound(int resId) throws IOException {
        mTeam1GoalPlayer = setUpPlayer(resId);
    }

    public void setTeam1WinSound(int resId) throws IOException  {
        mTeam1WinPlayer = setUpPlayer(resId);
    }

    public void setTeam2GoalSound(int resId) throws IOException  {
        mTeam2GoalPlayer = setUpPlayer(resId);
    }

    public void setTeam2WinPlayer(int resId) throws IOException  {
        mTeam2WinPlayer = setUpPlayer(resId);
    }

    private MediaPlayer setUpPlayer(int resId) throws IOException {
        MediaPlayer mediaPlayer = new MediaPlayer();
        // TODO: find out default volume values.setVolume() call might be redundant.
        mediaPlayer.create(mContext, resId)
                .setVolume(100, 100);

        mediaPlayer.prepare();
        return mediaPlayer;
    }

    public void release() {
        mTeam1GoalPlayer.release();
        mTeam1WinPlayer.release();
        mTeam2GoalPlayer.release();
        mTeam2WinPlayer.release();
    }
}
