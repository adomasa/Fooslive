package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.constants.Team;

import java.util.LinkedList;
import java.util.Queue;

public class PositionChecker {
    // TODO: Set value from app.config
    private static float sRealTableWidth;
    // TODO: Set value from app.config
    private static float sRealTableHeight;
    // TODO: Set value from app.config
    private static int sGoalFramesToCountGoal;

    private RectF mTeam2Zone;
    private RectF mTeam1Zone;

    //TODO delete redundant mGoalOccured member
    private boolean mGoalOccured;
    private long mTimestampStart;

    private Queue<Goal> mGoals;

    private boolean mBallInTeam2Zone = false;
    private boolean mBallInTeam1Zone = false;
    private int mFramesLost;

    public PositionChecker() {
        mGoals = new LinkedList<>();
    }

    public void onNewFrame(PointF lastBallCoordinates, GameController gameController) {
        // Check if this particular point signals that the ball is lost
        if (lastBallCoordinates == null) {
            if (mFramesLost == sGoalFramesToCountGoal) {
                // It is, so check if a goal is about to occur
                // TODO: Investigate if everything is ok here
                assignGoal(gameController);
            }
            else mFramesLost++;
        }
        else mGoalOccured = false;

        // It isn't, so reset the counter
        mFramesLost = 0;

        // Check if the ball is in either of the zones
        mBallInTeam1Zone = mTeam1Zone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
        mBallInTeam2Zone = mTeam2Zone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
    }

    private void assignGoal(GameController gameController) {
        if (!mBallInTeam1Zone || !mBallInTeam2Zone) return;

        com.unixonly.fooslive.gamecontrol.Goal toSetGoal;
        RectF toSetZone = new RectF(mTeam2Zone.left,
                                    mTeam2Zone.top,
                                    mTeam1Zone.right,
                                    mTeam1Zone.bottom);


        @Team.Type int goalTeam = (mBallInTeam1Zone) ? Team.TEAM_1 : Team.TEAM_2;

        gameController.incrementScore(goalTeam);
        gameController.goalListener.onGoal(goalTeam);
        toSetGoal = new Goal(gameController.getBallCoordinates(), toSetZone, goalTeam);


        mGoals.add(toSetGoal);

        // Reset variables to their starting values
        mFramesLost = 0;
        mBallInTeam2Zone = false;
        mBallInTeam1Zone = false;
        mGoalOccured = true;
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
