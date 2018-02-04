package com.unixonly.fooslive.drawing;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by paulius on 2/4/18.
 */

public class TextEffects {
    // TODO: Set value from app.config
    private static int mSlidingTextDelay;

    private static boolean mTextThreadStarted;

    public static void slideText(final String text,
                                 Activity activity,
                                 final TextView textView,
                                 final int maxLength) {
        if (mTextThreadStarted)
            return;

        mTextThreadStarted = true;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String temp = text;
                StringBuilder tempView = new StringBuilder(temp.length());

                for (int i = 0; i < maxLength; i++)
                {
                    tempView.append(' ');
                }
                textView.setText(tempView.toString());

                for (int i = 0; i < tempView.length() * 3; i++)
                {
                    tempView.delete(0,1);
                    tempView.append(i < temp.length() ? temp.charAt(i) : ' ');

                    textView.setText(tempView.toString());
                    try {
                        Thread.sleep(mSlidingTextDelay);
                    } catch (InterruptedException e) {
                        mTextThreadStarted = false;
                        return;
                    }
                }

                mTextThreadStarted = false;
            }
        });
    }
}
