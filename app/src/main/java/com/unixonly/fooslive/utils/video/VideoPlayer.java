package com.unixonly.fooslive.utils.video;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class VideoPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener ,
        MediaPlayer.OnCompletionListener {
    private boolean mDisposed;

    private Activity mActivity;

    public VideoPlayer(Context context) {
        mActivity = (Activity)context;
        mDisposed = false;
        try {
            setDataSource(context, mActivity.getIntent().getData());
            // TODO: Assign a surface to VideoPlayer
            /* super.setSurface(mActivity.SurfaceManager.Surface); */;
            prepare();
        } catch (IOException e) {
            // TODO: Rework this exception handling
            e.printStackTrace();
        }
        super.setOnPreparedListener(this);
        super.setOnCompletionListener(this);
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!mDisposed)
        {
            mediaPlayer.release();
            mDisposed = true;
        }
        // TODO
        /* _activity.ShowEndGameScreen(); */
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        // Load video
        mediaPlayer.start();

        // We only need the frames from the video, so mute the sound
        mediaPlayer.setVolume(0, 0);

        // Pause the video to let the user choose an Hsv value
        mediaPlayer.pause();
    }
}
