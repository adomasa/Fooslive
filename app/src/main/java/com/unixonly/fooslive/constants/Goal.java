package com.unixonly.fooslive.constants;

import android.support.annotation.IntDef;

public final class Goal {
    public static final int TEAM_2_GOAL = 0;
    public static final int TEAM_1_GOAL = 1;

    @IntDef({TEAM_2_GOAL, TEAM_1_GOAL})
    public @interface Type {}
}