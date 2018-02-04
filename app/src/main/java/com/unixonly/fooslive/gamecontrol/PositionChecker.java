package com.unixonly.fooslive.gamecontrol;

import android.graphics.RectF;

import com.unixonly.fooslive.util.UnitUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by paulius on 2/4/18.
 */

public class PositionChecker {
    // TODO: Set value from app.config
    private static float REAL_TABLE_WIDTH;
    // TODO: Set value from app.config
    private static float REAL_TABLE_HEIGHT;
    // TODO: Set value from app.config
    private static int GOAL_FRAMES_TO_COUNT_GOAL;

    private double mMulX;
    private double mMulY;

    private RectF mZoneOne;
    private RectF mZoneTwo;

    private boolean mGoalOccured;
    private long mTimestampStart;

    private Queue<Goal> mGoals;

    private boolean mBallInFirstGoalZone = false;
    private boolean mBallInSecondGoalZone = false;
    private int mFramesLost;

    public PositionChecker() {
        mGoals = new LinkedList<>();
    }

    private void CalculateMultipliers()
    {
        if (mZoneOne == null || mZoneTwo == null)
            return;

        mMulX = UnitUtils.metersToCentimeters(1) * ( REAL_TABLE_WIDTH / (mZoneTwo.right - mZoneTwo.left) );
        mMulY = UnitUtils.metersToCentimeters(1) * ( REAL_TABLE_HEIGHT / (mZoneTwo.bottom - mZoneOne.top) );
    }

    // TODO: Finish this class
}
