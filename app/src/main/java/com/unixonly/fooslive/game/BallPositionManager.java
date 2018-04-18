package com.unixonly.fooslive.game;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.model.Team;
import com.unixonly.fooslive.config.ConfigManager;

import java.util.LinkedList;
import java.util.Queue;

public class BallPositionManager {
    /**
     * Defines the number of frames a ball has to be lost in order for it to be counted a goal event
     */
    private final int goalFrames = ConfigManager.getInt("game.goal_frames");

    private RectF mTeam1GoalZone;
    private RectF mTeam2GoalZone;

    // TODO: Implement this once a timer is implemented
    private long mTimestampStart;

    /**
     * Holds game goal events including data associated with them
     */
    private Queue<Goal> mGoals = new LinkedList<>();

    private boolean mBallInTeam1Zone = false;
    private boolean mBallInTeam2Zone = false;

    private int mFramesLost;

    public void onNewFrame(PointF lastBallCoordinates, GameController gameController) {
        // Check if this particular point signals that the ball is lost
        if (lastBallCoordinates == null) {
            if (mFramesLost == goalFrames) {
                // It is, so check if a goal is about to occur
                // TODO: Investigate if everything is ok here
                assignGoal(gameController);
            }
            else mFramesLost++;
            return;
        }

        // It isn't, so reset the counter
        mFramesLost = 0;

        // Check if the ball is in either of the zones
        mBallInTeam1Zone = mTeam1GoalZone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
        mBallInTeam2Zone = mTeam2GoalZone.contains(lastBallCoordinates.x, lastBallCoordinates.y);
    }

    private void assignGoal(GameController gameController) {
        if (!mBallInTeam1Zone || !mBallInTeam2Zone) return;

        RectF toSetZone = new RectF(mTeam2GoalZone.left,
                                    mTeam2GoalZone.top,
                                    mTeam1GoalZone.right,
                                    mTeam1GoalZone.bottom);


        @Team.Type int goalTeam = (mBallInTeam1Zone) ? Team.TEAM_1 : Team.TEAM_2;

        gameController.incrementScore(goalTeam);
        gameController.goalListener.onGoal(goalTeam);

        mGoals.add(new Goal(gameController.getBallCoordinates(), toSetZone, goalTeam));

        resetSessionData();
    }

    private void resetSessionData() {
        mFramesLost = 0;
        mBallInTeam2Zone = false;
        mBallInTeam1Zone = false;
    }

    public RectF getTeam1GoalZone() {
        return mTeam1GoalZone;
    }

    public void setTeam1GoalZone(RectF zone) {
        mTeam1GoalZone = zone;
    }

    public RectF getTeam2GoalZone() {
        return mTeam2GoalZone;
    }

    public void setTeam2GoalZone(RectF zone) {
        mTeam2GoalZone = zone;
    }

    public Queue<Goal> getGoals() {
        return mGoals;
    }
}
