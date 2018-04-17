package com.unixonly.fooslive.game;

import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.unixonly.fooslive.game.model.Team;
import com.unixonly.fooslive.utils.ConfigManager;

import java.util.LinkedList;
import java.util.Queue;

import static com.unixonly.fooslive.utils.UnitUtils.calculateSpeed;
import static com.unixonly.fooslive.utils.UnitUtils.metersToCentimeters;

public class GameController {
    private static final String TAG = "GameController";
    private static final int TABLE_CORNER_COUNT = 4;

    /**
     * Table width in centimeters
     */
    private final double tableWidth = ConfigManager.getDouble("game.width_table");

    /**
     * Table height in centimeters
     */
    private final double tableHeight = ConfigManager.getDouble("game.height_table");

    /**
     * Defines how many ball positions we hold in memory
     */
    private final int maxBallMemory = ConfigManager.getInt("game.ball_pos_mem");

    /**
     * Defines the percentage of the top and bottom of the display to be reserved for the goal zone
     */
    private final float goalZoneSize = ConfigManager.getFloat("game.goal_zone");

    OnGoalEventListener goalListener;

    private HeatMap mHeatmapZones;

    private double mMulX;
    private double mMulY;

    private int mTeam1Score;
    private int mTeam2Score;

    private BallPositionManager mBallPositionManager = new BallPositionManager();

    private PointF[] mLastBallCoordinates = new PointF[2];
    private Queue<PointF> mBallCoordinates = new LinkedList<>();

    private double mAverageSpeed;
    private long mAverageSpeedCount;
    private double mMaxSpeed;

    /***
     * Calculates the goal zone boundaries using the points given
     * @param points The 4 corner points of the table
     */
    public void setTable(PointF[] points) {
        if (points.length != TABLE_CORNER_COUNT) return;

        RectF team1Zone = new RectF(points[0].x,
                mBallPositionManager.getTeam2GoalZone().bottom +
                        (1.0f - goalZoneSize * 2) * (points[2].y - points[0].y),
                points[3].x,
                points[3].y);

        RectF team2Zone = new RectF(points[0].x,
                points[0].y,
                points[1].x,
                points[1].y + (points[2].y - points[0].y) * goalZoneSize);

        // Calculate the different zones, using the points given
        mBallPositionManager.setTeam1GoalZone(team1Zone);
        mBallPositionManager.setTeam2GoalZone(team2Zone);
        mMulX = metersToCentimeters(1) * (tableWidth / (team1Zone.right - team1Zone.left));
        mMulY = metersToCentimeters(1) * (tableHeight / (team1Zone.bottom - team2Zone.top));

        RectF table = new RectF(
                mBallPositionManager.getTeam2GoalZone().left,
                mBallPositionManager.getTeam2GoalZone().top,
                mBallPositionManager.getTeam1GoalZone().right,
                mBallPositionManager.getTeam1GoalZone().bottom);

        mHeatmapZones = new HeatMap(table);
    }

    public PointF getLastBallCoordinates() {
        return mLastBallCoordinates[0];
    }

    //TODO: review code in detail
    public void setLastBallCoordinates(@Nullable PointF point) {
        if (mBallCoordinates.size() == maxBallMemory) mBallCoordinates.remove();

        mLastBallCoordinates[1] = mLastBallCoordinates[0];
        mLastBallCoordinates[0] = point;

        mHeatmapZones.setValue(point);

        mBallCoordinates.add(point);

        mBallPositionManager.onNewFrame(point, this);

        double currentSpeed = calculateSpeed(mLastBallCoordinates[0], mLastBallCoordinates[1],
                mMulX, mMulY);

        if (point != null) {
            mAverageSpeed = ((mAverageSpeed * mAverageSpeedCount) + currentSpeed)
                    / (mAverageSpeedCount);
            mAverageSpeedCount++;
        }

        if (mMaxSpeed < currentSpeed) mMaxSpeed = currentSpeed;
    }

    public HeatMap getZones() {
        return mHeatmapZones;
    }

    public Queue<PointF> getBallCoordinates() {
        return mBallCoordinates;
    }

    public BallPositionManager getPositionChecker() {
        return mBallPositionManager;
    }

    public int getTeam1Score() {
        return mTeam1Score;
    }

    public int getTeam2Score() {
        return mTeam2Score;
    }

    /**
     * Increment score by 1
     */
    public void incrementScore(@Team.Type int team) {
        if (team == Team.TEAM_1) mTeam1Score++;
        else mTeam2Score++;
    }

    /**
     * Decrement score by 1
     */
    public void descrementScore(@Team.Type int team) {
        if (team == Team.TEAM_1) mTeam1Score--;
        else mTeam2Score--;
    }

    public void setOnGoalEventListener(OnGoalEventListener listener) {
        goalListener = listener;
    }
}
