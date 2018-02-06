package com.unixonly.fooslive.util.video;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by paulius on 2/6/18.
 */

public class VideoPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener ,
        MediaPlayer.OnCompletionListener {
    private boolean mDisposed;

    private Activity mActivity;

    public VideoPlayer(Context context) {
        mActivity = (Activity)context;
        mDisposed = false;
        try {
            super.setDataSource(context, mActivity.getIntent().getData());
            // TODO: Assign a surface to VideoPlayer
            /* super.setSurface(mActivity.SurfaceManager.Surface); */;
            super.prepare();
        } catch (IOException e) {
            // TODO: Rework this exception handling
            e.printStackTrace();
        }
        super.setOnPreparedListener(this);
        super.setOnCompletionListener(this);
    }

    public void onCompletion(MediaPlayer mediaPlayer)
    {
        if (!mDisposed)
        {
            mediaPlayer.release();
            mDisposed = true;
        }
        // TODO
        /* _activity.ShowEndGameScreen(); */
    }
    public void release()
    {
        if (mDisposed)
            return;

        super.release();
        mDisposed = true;
    }
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        // Load video
        mediaPlayer.start();

        // We only need the frames from the video, so mute the sound
        mediaPlayer.setVolume(0, 0);

        // Pause the video to let the user choose an Hsv value
        mediaPlayer.pause();
    }
}
