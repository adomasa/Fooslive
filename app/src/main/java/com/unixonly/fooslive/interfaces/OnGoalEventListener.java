package com.unixonly.fooslive.interfaces;

import com.unixonly.fooslive.constants.GoalEventType;

public interface OnGoalEventListener {
    void onGoal(@GoalEventType.GoalType int eventType);
}
