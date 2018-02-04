package com.unixonly.fooslive.gamecontrol;

import com.unixonly.fooslive.interfaces.OnGoalEventListener;

/**
 * Created by paulius on 2/4/18.
 */

public class GameController {
    // TODO: Set value from app.config
    private static int MAXIMUM_BALL_COORDINATE_NUMBER;
    // TODO: Set value from app.config
    private static int HEAT_MAP_ZONES_WIDTH;
    // TODO: Set value from app.config
    private static int HEAT_MAP_ZONES_HEIGHT;

    private OnGoalEventListener mListener;

    private ZoneInfo mZones;

    private int mRedScore;
    private int mBlueScore;


}
