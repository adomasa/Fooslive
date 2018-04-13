package com.unixonly.fooslive.utils.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.unixonly.fooslive.game.model.Alert;

import java.io.IOException;

/**
 * Use .release() when finished using the object to release memory
 */
public class SoundAlerts {
    public static final String TAG = "SoundAlerts";
    private Context mContext;

    private MediaPlayer mTeam1GoalPlayer;
    private MediaPlayer mTeam1WinPlayer;

    private MediaPlayer mTeam2GoalPlayer;
    private MediaPlayer mTeam2WinPlayer;

    public SoundAlerts(Context context, int team1GoalSoundId, int team1WinSoundId,
                       int team2GoalSoundId, int team2WinSoundId)
            throws IOException {
        mContext = context;
        mTeam1GoalPlayer = setUpPlayer(team1GoalSoundId);
        mTeam1WinPlayer = setUpPlayer(team1WinSoundId);
        mTeam2GoalPlayer = setUpPlayer(team2GoalSoundId);
        mTeam2WinPlayer = setUpPlayer(team2WinSoundId);

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

    private MediaPlayer setUpPlayer(int resId) throws IOException {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, resId);
        // TODO: find out default volume values.setVolume() call might be redundant.
        mediaPlayer.setVolume(100, 100);

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
