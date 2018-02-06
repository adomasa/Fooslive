package com.unixonly.fooslive.util.sound;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by paulius on 2/6/18.
 */

class SoundPlayer {
    private MediaPlayer mPlayer;

    SoundPlayer(Context context, int resId) {
        mPlayer = MediaPlayer.create(context, resId);
        mPlayer.setVolume(100,100);
        try {
            mPlayer.prepare();
        } catch (IOException e) {
            // TODO: Assign a default value here if no file is found
            e.printStackTrace();
        }
    }

    void play() {
        mPlayer.start();
    }
}
