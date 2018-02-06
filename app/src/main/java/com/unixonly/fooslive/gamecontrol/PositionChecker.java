package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.enums.EGoalEvent;
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

    private void calculateMultipliers()
    {
        if (mZoneOne == null || mZoneTwo == null)
            return;

        mMulX = UnitUtils.metersToCentimeters(1) * ( REAL_TABLE_WIDTH / (mZoneTwo.right - mZoneTwo.left) );
        mMulY = UnitUtils.metersToCentimeters(1) * ( REAL_TABLE_HEIGHT / (mZoneTwo.bottom - mZoneOne.top) );
    }

    public void onNewFrame(PointF lastBallCoordinates,
                           GameController gameController) {
        // Check if this particular point signals that the ball is lost
        if (lastBallCoordinates == null)
        {
            if (mFramesLost == GOAL_FRAMES_TO_COUNT_GOAL)
            {
                // It is, so check if a goal is about to occur
                if (mBallInFirstGoalZone)
                {
                    // Fire the goal event for the first team
                    gameController.setBlueScore(gameController.getBlueScore() + 1);
                    gameController.fireGoalEvent(EGoalEvent.BlueGoal);

                    mGoals.add(new Goal(gameController.getBallCoordinates(),
                            new RectF(mZoneOne.left,
                                    mZoneOne.top,
                                    mZoneTwo.right,
                                    mZoneTwo.bottom),
                                    mTimestampStart,
                                    GameTimer.sTime,
                                    true));

                    // Reset variables to their starting values
                    mFramesLost = 0;
                    mBallInFirstGoalZone = false;
                    mBallInSecondGoalZone = false;
                    mGoalOccured = true;

                    return;
                }

                if (!mBallInSecondGoalZone)
                    return;

                // Fire the goal event for the second team
                gameController.setRedScore(gameController.getRedScore() + 1);
                gameController.fireGoalEvent(EGoalEvent.RedGoal);

                mGoals.add(new Goal(gameController.getBallCoordinates(),
                        new RectF(mZoneOne.left,
                                mZoneOne.top,
                                mZoneTwo.right,
                                mZoneTwo.bottom),
                                mTimestampStart,
                                GameTimer.sTime,
                                false));

                // Reset variables to their starting values
                mFramesLost = 0;
                mBallInFirstGoalZone = false;
                mBallInSecondGoalZone = false;
                mGoalOccured = true;
            }
            else
                mFramesLost ++;
        }
        else
        {
            if (mGoalOccured)
            {
                mTimestampStart = GameTimer.sTime;
                mGoalOccured = false;
            }

            // It isn't, so reset the counter
            mFramesLost = 0;

            // Check if the ball is in the first zone
            if (mZoneOne.contains(lastBallCoordinates.x, lastBallCoordinates.y))
            {
                mBallInFirstGoalZone = true;
            }
            else
                // Check if the ball is in the second zone
                if (mZoneTwo.contains(lastBallCoordinates.x, lastBallCoordinates.y))
                {
                    mBallInSecondGoalZone = true;
                }
                else
                {
                    mBallInFirstGoalZone = false;
                }
        }
    }

    public double calculateSpeed(PointF one, PointF two)
    {
        if (one == null || two == null)
            return 0;
        else
            return Math.sqrt(
                (one.x * mMulX - two.x * mMulX) * (one.x * mMulX - two.x * mMulX) +
                        (one.y * mMulY - two.y * mMulY) * (one.y * mMulY - two.y * mMulY));
    }

    public void setZoneOne(RectF zone) {
        mZoneOne = zone;
        calculateMultipliers();
    }

    public void setZoneTwo(RectF zone) {
        mZoneTwo = zone;
        calculateMultipliers();
    }

    RectF getZoneOne() {
        return mZoneOne;
    }

    RectF getZoneTwo() {
        return mZoneTwo;
    }
}
