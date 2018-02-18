package com.unixonly.fooslive.drawing;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

/**
 * This class is responsible for displaying various
 * text effects. For now, a sliding text animation is
 * implemented
 */
public class TextEffects {
    public static final String TAG = "TextEffects";

    // TODO: Set value from app.config
    private static int mSlidingTextDelay;

    private boolean mTextThreadStarted;

    private final Activity mActivity;
    private final TextView mTextEventSlider;
    private final int mMaxLength;

    /**
     * @param activity
     * The activity, which is used to access the UI thread
     * @param textEventSlider
     * The textView, on which to display the animation
     * @param maxLength
     * The max length of the text view ( in characters )
     */
    public TextEffects(Activity activity, TextView textEventSlider, int maxLength) {
        mActivity = activity;
        mTextEventSlider = textEventSlider;
        mMaxLength = maxLength;
    }

    /**
     * Displays the sliding text animation on the TextView given
     * @param text
     * The text, which will be animated
     */
    public synchronized void slideText(final String text) {
        String temp = text;
        final StringBuilder tempView = new StringBuilder(temp.length());

        for (int i = 0; i < mMaxLength; i++) {
            tempView.append(' ');
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextEventSlider.setText(tempView.toString());
            }
        });

        for (int i = 0; i < tempView.length() * 3; i++) {
            tempView.delete(0,1);
            tempView.append(i < temp.length() ? temp.charAt(i) : ' ');

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextEventSlider.setText(tempView.toString());
                }
            });

            try {
                wait(mSlidingTextDelay);
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
                return;
            }
        }
    }
}
