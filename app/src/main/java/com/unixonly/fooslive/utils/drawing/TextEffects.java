package com.unixonly.fooslive.utils.drawing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.unixonly.fooslive.utils.ConfigManager;

public class TextEffects {
    public static final String TAG = "TextEffects";

    private final int mSlidingTextDelay;
    private final int mDefaultAnimSpace;

    private final Activity mActivity;
    private final TextView mTextEventSlider;

    //TODO: based of the responsibility of the class, it generates various effects. However,
    // you have to create new instance for every textview you have. That's a memory leak.
    /**
     * @param context used to access the UI thread
     * @param textEventSlider the textView, on which to display the animation
     */
    public TextEffects(Context context, TextView textEventSlider) {
        mSlidingTextDelay = ConfigManager.getInt("ui.slider_delay");
        mDefaultAnimSpace = ConfigManager.getInt("ui.slider_max_length");
        mActivity = (Activity) context;
        mTextEventSlider = textEventSlider;
    }

    /**
     * Animate text appearance on given text view using sliding effect
     * @param text content, which will appear
     */
    public synchronized void slideText(final String text) {
        final StringBuilder tempView = new StringBuilder(text.length());

        int animSpace = mDefaultAnimSpace > text.length() ? mDefaultAnimSpace : text.length();

        for (int i = 0; i < animSpace; i++) {
            tempView.append(' ');
        }

        mActivity.runOnUiThread(() -> mTextEventSlider.setText(tempView.toString()));

        for (int i = 0; i < tempView.length() * 3; i++) {
            tempView.deleteCharAt(0);

            tempView.append(i < text.length() ? text.charAt(i) : ' ');

            mActivity.runOnUiThread(() -> mTextEventSlider.setText(tempView.toString()));

            //TODO: modify the code based on warnings
            try {
                wait(mSlidingTextDelay);
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
                //TODO: test this part
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
