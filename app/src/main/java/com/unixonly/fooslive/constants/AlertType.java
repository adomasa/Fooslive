package com.unixonly.fooslive.constants;

import android.support.annotation.IntDef;

public final class AlertType {
    public static final int TEAM_1_GOAL = 0;
    public static final int TEAM_1_WIN = 1;
    public static final int TEAM_2_GOAL = 2;
    public static final int TEAM_2_WIN = 3;

    @IntDef({TEAM_1_GOAL, TEAM_1_WIN, TEAM_2_GOAL, TEAM_2_WIN})
    public @interface Type {}
}
