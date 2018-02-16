package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.constants.GoalEventType;
import com.unixonly.fooslive.constants.TeamType;
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

    private RectF mTeam2Zone;
    private RectF mTeam1Zone;

    private boolean mGoalOccured;
    private long mTimestampStart;

    private Queue<Goal> mGoals;

    private boolean mBallInTeam2Zone = false;
    private boolean mBallInTeam1Zone = false;
    private int mFramesLost;

    public PositionChecker() {
        mGoals = new LinkedList<>();
    }

    public void onNewFrame(PointF lastBallCoordinates,
                           GameController gameController) {
        // Check if this particular point signals that the ball is lost
        if (lastBallCoordinates == null) {
            if (mFramesLost == sGoalFramesToCountGoal) {
                // It is, so check if a goal is about to occur
                // TODO: Investigate if everything is ok here
                if (mBallInTeam2Zone) {
                    // Fire the goal event for the first team
                    gameController.setTeam2Score(gameController.getTeam2Score() + 1);
                    gameController.fireGoalEvent(GoalEventType.TEAM_2_GOAL);

                    mGoals.add(new Goal(gameController.getBallCoordinates(),
                            new RectF(mTeam2Zone.left,
                                    mTeam2Zone.top,
                                    mTeam1Zone.right,
                                    mTeam1Zone.bottom),
                                    TeamType.TEAM_2));

                    // Reset variables to their starting values
                    mFramesLost = 0;
                    mBallInTeam2Zone = false;
                    mBallInTeam1Zone = false;
                    mGoalOccured = true;

                    return;
                }

                if (!mBallInTeam1Zone) return;

                // Fire the goal event for the second team
                gameController.setTeam1Score(gameController.getTeam1Score() + 1);
                gameController.fireGoalEvent(GoalEventType.TEAM_1_GOAL);

                mGoals.add(new Goal(gameController.getBallCoordinates(),
                        new RectF(mTeam2Zone.left,
                                mTeam2Zone.top,
                                mTeam1Zone.right,
                                mTeam1Zone.bottom),
                                TeamType.TEAM_1));

                // Reset variables to their starting values
                mFramesLost = 0;
                mBallInTeam2Zone = false;
                mBallInTeam1Zone = false;
                mGoalOccured = true;
            }
            else
                mFramesLost ++;
        }
        else {
            if (mGoalOccured) {
                mGoalOccured = false;
            }

            // It isn't, so reset the counter
            mFramesLost = 0;

            // Check if the ball is in either of the zones
            mBallInTeam2Zone = mTeam2Zone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
            mBallInTeam1Zone = mTeam1Zone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
        }
    }

    /**
     * A setter for the first team's goal zone
     * @param zone
     * A RectF, containing the coordinates of the zone
     */
    public void setTeam2Zone(RectF zone) {
        mTeam2Zone = zone;
    }

    /**
     * A setter for the second team's goal zone
     * @param zone
     * A RectF, containing the coordinates of the zone
     */
    public void setTeam1Zone(RectF zone) {
        mTeam1Zone = zone;
    }

    RectF getTeam2Zone() {
        return mTeam2Zone;
    }

    RectF getTeam1Zone() {
        return mTeam1Zone;
    }
}
