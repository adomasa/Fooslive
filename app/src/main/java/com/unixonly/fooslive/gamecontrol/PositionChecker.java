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
    private static float sRealTableWidth;
    // TODO: Set value from app.config
    private static float sRealTableHeight;
    // TODO: Set value from app.config
    private static int sGoalFramesToCountGoal;

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

        mMulX = UnitUtils.metersToCentimeters(1) * ( sRealTableWidth / (mZoneTwo.right - mZoneTwo.left) );
        mMulY = UnitUtils.metersToCentimeters(1) * ( sRealTableHeight / (mZoneTwo.bottom - mZoneOne.top) );
    }

    public void onNewFrame(PointF lastBallCoordinates,
                           GameController gameController) {
        // Check if this particular point signals that the ball is lost
        if (lastBallCoordinates == null) {
            if (mFramesLost == sGoalFramesToCountGoal) {
                // It is, so check if a goal is about to occur
                // TODO: Investigate if everything is ok here
                if (mBallInFirstGoalZone) {
                    // Fire the goal event for the first team
                    gameController.setTeam2Score(gameController.getTeam2Score() + 1);
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

                if (!mBallInSecondGoalZone) return;

                // Fire the goal event for the second team
                gameController.setTeam1Score(gameController.getTeam1Score() + 1);
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
        else {
            if (mGoalOccured) {
                mTimestampStart = GameTimer.sTime;
                mGoalOccured = false;
            }

            // It isn't, so reset the counter
            mFramesLost = 0;

            // Check if the ball is in either of the zones
            mBallInFirstGoalZone = mZoneOne.contains(lastBallCoordinates.x, lastBallCoordinates.y);
            mBallInSecondGoalZone = mZoneTwo.contains(lastBallCoordinates.x, lastBallCoordinates.y);
        }
    }

    /**
     * Calculates the difference between two points
     * @param one
     * The coordinates of the first point
     * @param two
     * The coordinates of the second point
     * @return
     * The difference between the points
     */
    public double calculateSpeed(PointF one, PointF two)
    {
        double toReturn = 0;

        if (one == null || two == null) toReturn = 0;
        else toReturn = Math.sqrt(
                        (one.x * mMulX - two.x * mMulX) * (one.x * mMulX - two.x * mMulX) +
                        (one.y * mMulY - two.y * mMulY) * (one.y * mMulY - two.y * mMulY));

        return toReturn;
    }

    /**
     * A setter for the first team's goal zone
     * @param zone
     * A RectF, containing the coordinates of the zone
     */
    public void setZoneOne(RectF zone) {
        mZoneOne = zone;
        calculateMultipliers();
    }

    /**
     * A setter for the second team's goal zone
     * @param zone
     * A RectF, containing the coordinates of the zone
     */
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
