package com.unixonly.fooslive.interfaces;

import com.unixonly.fooslive.constants.Goal;

public interface OnGoalEventListener {
    void onGoal(@Goal.Type int eventType);
}
