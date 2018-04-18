package com.unixonly.fooslive.model;

import android.support.annotation.IntDef;

public final class Alert {
    public static final int GOAL = 0;
    public static final int WIN = 1;

    @IntDef({GOAL, WIN})
    public @interface Type {}
}
