package com.unixonly.fooslive.utils.drawing;

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

    private final Activity mActivity;
    private final TextView mTextEventSlider;
    private final int mMaxLength;

    /**
     * @param activity the activity, which is used to access the UI thread
     * @param textEventSlider the textView, on which to display the animation
     * @param maxLength the max length of the text view (in characters)
     */
    public TextEffects(Activity activity, TextView textEventSlider, int maxLength) {
        mActivity = activity;
        mTextEventSlider = textEventSlider;
        mMaxLength = maxLength;
    }

    /**
     * Displays the sliding text animation on the TextView given
     * @param text The text, which will be animated
     */
    public synchronized void slideText(final String text) {
        final StringBuilder tempView = new StringBuilder(text.length());

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
            tempView.deleteCharAt(0);

            tempView.append(i < text.length() ? text.charAt(i) : ' ');

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
