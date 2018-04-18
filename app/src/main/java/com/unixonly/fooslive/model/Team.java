package com.unixonly.fooslive.model;

import android.support.annotation.IntDef;

public final class Team {
    public static final int TEAM_1 = 0;
    public static final int TEAM_2 = 1;

    @IntDef({TEAM_1, TEAM_2})
    public @interface Type {}
}
