package com.unixonly.fooslive.game;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.game.model.Team;
import com.unixonly.fooslive.utils.ConfigManager;

import java.util.LinkedList;
import java.util.Queue;

public class PositionChecker {
    // Defines the number of frames a ball has to be lost in order for it to be counted a goal event
    private final int goalFrames;

    // Defines the rectangle, containing the Team 2 goal zone
    private RectF mTeam2Zone;
    // Defines the rectangle, containing the Team 1 goal zone
    private RectF mTeam1Zone;

    // TODO: Implement this once a timer is implemented
    private long mTimestampStart;

    // Holds historic goal events, including data associated with them
    private Queue<Goal> mGoals;

    // Defines whether the ball has appeared in a specific team's zone
    private boolean mBallInTeam2Zone = false;
    private boolean mBallInTeam1Zone = false;

    // How many previous frames a ball was lost
    private int mFramesLost;

    public PositionChecker() {
        goalFrames = ConfigManager.getInt("game.goal_frames");
        mGoals = new LinkedList<>();
    }

    public void onNewFrame(PointF lastBallCoordinates, GameController gameController) {
        // Check if this particular point signals that the ball is lost
        if (lastBallCoordinates == null) {
            if (mFramesLost == goalFrames) {
                // It is, so check if a goal is about to occur
                // TODO: Investigate if everything is ok here
                assignGoal(gameController);
            }
            else mFramesLost++;
        }

        // It isn't, so reset the counter
        mFramesLost = 0;

        // Check if the ball is in either of the zones
        mBallInTeam1Zone = mTeam1Zone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
        mBallInTeam2Zone = mTeam2Zone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
    }

    private void assignGoal(GameController gameController) {
        if (!mBallInTeam1Zone || !mBallInTeam2Zone) return;

        com.unixonly.fooslive.game.Goal toSetGoal;
        RectF toSetZone = new RectF(mTeam2Zone.left,
                                    mTeam2Zone.top,
                                    mTeam1Zone.right,
                                    mTeam1Zone.bottom);


        @Team.Type int goalTeam = (mBallInTeam1Zone) ? Team.TEAM_1 : Team.TEAM_2;

        gameController.incrementScore(goalTeam);
        gameController.goalListener.onGoal(goalTeam);
        toSetGoal = new Goal(gameController.getBallCoordinates(), toSetZone, goalTeam);


        mGoals.add(toSetGoal);

        resetSessionData();
    }

    private void resetSessionData() {
        mFramesLost = 0;
        mBallInTeam2Zone = false;
        mBallInTeam1Zone = false;
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

    RectF getTeam1Zone() {
        return mTeam1Zone;
    }

    RectF getTeam2Zone() {
        return mTeam2Zone;
    }

    public Queue<Goal> getGoals() {
        return mGoals;
    }
}
