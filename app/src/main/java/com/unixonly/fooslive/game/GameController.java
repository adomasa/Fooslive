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

    public final double tableWidth;
    public final double tableHeight;
    private final int maxBallMemory;
    private final float goalZoneSize;

    OnGoalEventListener goalListener;

    private HeatMap mHeatmapZones;

    private double mMulX;
    private double mMulY;

    private int mTeam1Score;
    private int mTeam2Score;

    private PositionChecker mPositionChecker;

    private PointF[] mLastBallCoordinates;
    private Queue<PointF> mBallCoordinates;

    private double mAverageSpeed;
    private long mAverageSpeedCount;
    private double mMaxSpeed;

    public GameController() {
        tableWidth = ConfigManager.getDouble("game.width_table");
        tableHeight = ConfigManager.getDouble("game.height_table");
        maxBallMemory = ConfigManager.getInt("game.ball_pos_mem");
        goalZoneSize = ConfigManager.getFloat("game.goal_zone");

        mBallCoordinates = new LinkedList<>();
        mPositionChecker = new PositionChecker();
        mLastBallCoordinates = new PointF[2];
    }

    /***
     * Calculates the goal zone boundaries using the points given
     * @param points The 4 corner points of the table
     */
    public void setTable(PointF[] points) {
        if (points.length != TABLE_CORNER_COUNT) return;

        RectF team1Zone = new RectF(points[0].x,
                mPositionChecker.getTeam2Zone().bottom +
                        (1.0f - goalZoneSize * 2) * (points[2].y - points[0].y),
                points[3].x,
                points[3].y);

        RectF team2Zone = new RectF(points[0].x,
                points[0].y,
                points[1].x,
                points[1].y + (points[2].y - points[0].y) * goalZoneSize);

        // Calculate the different zones, using the points given
        mPositionChecker.setTeam1Zone(team1Zone);
        mPositionChecker.setTeam2Zone(team2Zone);
        mMulX = metersToCentimeters(1) * (tableWidth / (team1Zone.right - team1Zone.left));
        mMulY = metersToCentimeters(1) * (tableHeight / (team1Zone.bottom - team2Zone.top));

        RectF table = new RectF(
                mPositionChecker.getTeam2Zone().left,
                mPositionChecker.getTeam2Zone().top,
                mPositionChecker.getTeam1Zone().right,
                mPositionChecker.getTeam1Zone().bottom);

        mHeatmapZones = new HeatMap(table);
    }

    public PointF getLastBallCoordinates() {
        return mLastBallCoordinates[0];
    }

    public void setLastBallCoordinates(@Nullable PointF point) {
        if (mBallCoordinates.size() == maxBallMemory) mBallCoordinates.remove();

        mLastBallCoordinates[1] = mLastBallCoordinates[0];
        mLastBallCoordinates[0] = point;

        mHeatmapZones.setValue(point);

        mBallCoordinates.add(point);

        mPositionChecker.onNewFrame(point, this);

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

    public PositionChecker getPositionChecker() {
        return mPositionChecker;
    }

    public void setTeam1Score(int score) {
        mTeam1Score = score;
    }

    public void setTeam2Score(int score) {
        mTeam2Score = score;
    }

    public int getTeam1Score() {
        return mTeam1Score;
    }

    public int getTeam2Score() {
        return mTeam2Score;
    }

    /**
     * Add score by 1 for team defined in the argument
     * @param team team identifier
     */
    public void incrementScore(@Team.Type int team) {
        if (team == Team.TEAM_1) mTeam1Score++;
        else mTeam2Score++;
    }

    public void setOnGoalEventListener(OnGoalEventListener listener) {
        goalListener = listener;
    }
}
