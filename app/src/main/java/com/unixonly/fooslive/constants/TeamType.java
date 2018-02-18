package com.unixonly.fooslive.constants;

import android.support.annotation.IntDef;

public final class TeamType {
    public static final int TEAM_1 = 0;
    public static final int TEAM_2 = 1;

    @IntDef({TEAM_1, TEAM_2})
    public @interface Team {}
}
