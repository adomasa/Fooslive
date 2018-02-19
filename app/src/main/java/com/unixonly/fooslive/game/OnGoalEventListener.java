package com.unixonly.fooslive.game;

import com.unixonly.fooslive.game.model.Team;

public interface OnGoalEventListener {
    void onGoal(@Team.Type int goalTeam);
}
