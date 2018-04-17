package com.unixonly.fooslive.game.model;

import android.support.annotation.IntDef;

public class Mode {
    public static final int LIVE = 0;
    public static final int RECORD = 1;

    @IntDef({LIVE, RECORD})
    public @interface Type {}
}
