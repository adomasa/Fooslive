package com.unixonly.fooslive.interfaces;

import com.unixonly.fooslive.constants.Team;

public interface OnGoalEventListener {
    void onGoal(@Team.Type int goalTeam);
}
