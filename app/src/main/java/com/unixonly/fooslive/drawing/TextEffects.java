package com.unixonly.fooslive.drawing;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by paulius on 2/4/18.
 */

/**
 * This class is responsible for displaying various
 * text effects. For now, a sliding text animation is
 * implemented
 */
public class TextEffects {
    // TODO: Set value from app.config
    private static int mSlidingTextDelay;

    private boolean mTextThreadStarted;

    private final Activity mActivity;
    private final TextView mEventSliderView;
    private final int mMaxLength;

    /**
     * @param activity
     * The activity, which is used to access the UI thread
     * @param textView
     * The textView, on which to display the animation
     * @param maxLength
     * The max length of the text view ( in characters )
     */
    public TextEffects(Activity activity, TextView textView, int maxLength) {
        mActivity = activity;
        mEventSliderView = textView;
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
                mEventSliderView.setText(tempView.toString());
            }
        });

        for (int i = 0; i < tempView.length() * 3; i++) {
            tempView.delete(0,1);
            tempView.append(i < temp.length() ? temp.charAt(i) : ' ');

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEventSliderView.setText(tempView.toString());
                }
            });

            try {
                this.wait(mSlidingTextDelay);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
