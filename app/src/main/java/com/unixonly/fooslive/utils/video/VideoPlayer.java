package com.unixonly.fooslive.utils.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.view.Surface;

import java.io.IOException;

public class VideoPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener ,
        MediaPlayer.OnCompletionListener {
    private boolean mDisposed;

    private Activity mActivity;

    public VideoPlayer(@NonNull Context context, SurfaceTexture texture) {
        mActivity = (Activity)context;
        mDisposed = false;
        try {
            setDataSource(context, mActivity.getIntent().getData());
            super.setSurface(new Surface(texture));
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
