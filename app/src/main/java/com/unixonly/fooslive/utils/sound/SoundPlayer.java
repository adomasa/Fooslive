package com.unixonly.fooslive.utils.sound;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

class SoundPlayer {
    private MediaPlayer mPlayer;

    SoundPlayer(Context context, int resId) throws IOException {
        mPlayer = MediaPlayer.create(context, resId);
        mPlayer.setVolume(100,100);
        mPlayer.prepare();
    }

    void play() {
        mPlayer.start();
    }
}
