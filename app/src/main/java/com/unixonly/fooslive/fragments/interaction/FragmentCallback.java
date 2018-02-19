package com.unixonly.fooslive.fragments.interaction;

import android.support.annotation.IntDef;


public final class FragmentCallback {
    public static final int ACTION_SET_TITLE = 0;
    public static final int ACTION_NAVIGATE_TO = 1;

    @IntDef({ACTION_SET_TITLE, ACTION_NAVIGATE_TO})
    public @interface Action {}
}
