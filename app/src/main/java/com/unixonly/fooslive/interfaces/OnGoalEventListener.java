package com.unixonly.fooslive.interfaces;

import com.unixonly.fooslive.constants.GoalEventType;

/**
 * Created by paulius on 2/4/18.
 */

public interface OnGoalEventListener {
    void onGoal(@GoalEventType.GoalType int eventType);
}
