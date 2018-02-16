package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.constants.GoalEventType;
import com.unixonly.fooslive.interfaces.OnGoalEventListener;
import com.unixonly.fooslive.util.MeasurementUtils;
import com.unixonly.fooslive.util.UnitUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by paulius on 2/4/18.
 */

public class GameController {
    // TODO: Set value from app.config
    public static double sRealTableWidth;
    // TODO: Set value from app.config
    public static double sRealTableHeight;

    private static final int TABLE_CORNER_COUNT = 4;
    // TODO: Set value from app.config
    private static int sMaximumBallCoordinateNumber;
    // TODO: Set value from app.config
    private static int sHeatMapZoneWidth;
    // TODO: Set value from app.config
    private static int sHeatMapZoneHeight;
    // TODO: Set value from app.config
    private static float sPercentageOfSide;

    private OnGoalEventListener mListener;

    private ZoneInfo mZones;

    private double mMulX;
    private double mMulY;

    private int mTeam1Score;
    private int mTeam2Score;

    private PositionChecker mPositionChecker;

    private PointF[] mLastBallCoordinates;
    private Queue<PointF> mBallCoordinates;

    private double mCurrentSpeed;
    private double mAverageSpeed;
    private long mAverageSpeedCount;
    private double mMaxSpeed;

    public GameController() {
        mBallCoordinates = new LinkedList<>();
        mPositionChecker = new PositionChecker();
    }

    public void setTable(PointF[] points) {
        if (points.length != TABLE_CORNER_COUNT) return;

        RectF team1Zone = new RectF(points[0].x,
                mPositionChecker.getTeam2Zone().bottom + (1.0f - sPercentageOfSide * 2) * (points[2].y - points[0].y),
                points[3].x,
                points[3].y);
        RectF team2Zone = new RectF(points[0].x,
                points[0].y,
                points[1].x,
                points[1].y + (points[2].y - points[0].y) * sPercentageOfSide);

        // Calculate the different zones, using the points given
        mPositionChecker.setTeam1Zone(team1Zone);
        mPositionChecker.setTeam2Zone(team2Zone);

        mMulX = UnitUtils.metersToCentimeters(1) * ( sRealTableWidth / (team1Zone.right - team1Zone.left) );
        mMulY = UnitUtils.metersToCentimeters(1) * ( sRealTableHeight / (team1Zone.bottom - team2Zone.top) );

        RectF table = new RectF(
                mPositionChecker.getTeam2Zone().left,
                mPositionChecker.getTeam2Zone().top,
                mPositionChecker.getTeam1Zone().right,
                mPositionChecker.getTeam1Zone().bottom);
        mZones = new ZoneInfo(
                table,
                sHeatMapZoneWidth,
                sHeatMapZoneHeight);
    }

    public PointF getLastBallCoordinates() {
        return mLastBallCoordinates[0];
    }

    public void setLastBallCoordinates(PointF point) {
        if (mBallCoordinates.size() == sMaximumBallCoordinateNumber) mBallCoordinates.remove();

        mLastBallCoordinates[1] = mLastBallCoordinates[0];
        mLastBallCoordinates[0] = point;

        mZones.assignValue(point);

        mBallCoordinates.add(point);

        mPositionChecker.onNewFrame(point,
                                    this);

        mCurrentSpeed = MeasurementUtils.calculateSpeed(mLastBallCoordinates[0],
                                                        mLastBallCoordinates[1],
                                                        mMulX,
                                                        mMulY);

        if (point != null) {
            mAverageSpeed = ((mAverageSpeed * mAverageSpeedCount)
                    + mCurrentSpeed) / (mAverageSpeedCount + 1);
            mAverageSpeedCount++;
        }

        if (mMaxSpeed < mCurrentSpeed) mMaxSpeed = mCurrentSpeed;
    }

    void fireGoalEvent(@GoalEventType.GoalType int eventType) {
        mListener.onGoal(eventType);
    }

    public ZoneInfo getZones() {
        return mZones;
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

    public void setOnGoalEventListener(OnGoalEventListener listener) {
        mListener = listener;
    }
}
