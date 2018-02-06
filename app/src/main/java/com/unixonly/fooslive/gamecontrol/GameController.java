package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.enums.ECaptureMode;
import com.unixonly.fooslive.enums.EGoalEvent;
import com.unixonly.fooslive.interfaces.OnGoalEventListener;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by paulius on 2/4/18.
 */

public class GameController {
    private static final int TABLE_POINT_NUMBER = 4;
    // TODO: Set value from app.config
    private static int MAXIMUM_BALL_COORDINATE_NUMBER;
    // TODO: Set value from app.config
    private static int HEAT_MAP_ZONES_WIDTH;
    // TODO: Set value from app.config
    private static int HEAT_MAP_ZONES_HEIGHT;
    // TODO: Set value from app.config
    private static float PERCENTAGE_OF_SIDE;

    private OnGoalEventListener mListener;

    private ZoneInfo mZones;

    private int mRedScore;
    private int mBlueScore;

    private PositionChecker mPositionChecker;

    private PointF[] lastBallCoordinates;
    private Queue<PointF> mBallCoordinates;

    private double mCurrentSpeed;
    private double mAverageSpeed;
    private long mAverageSpeedCounter;
    private double mMaxSpeed;

    public GameController() {
        mBallCoordinates = new LinkedList<>();
        mPositionChecker = new PositionChecker();
    }

    public void setTable(PointF[] points, ECaptureMode captureMode) {
        if (points.length != TABLE_POINT_NUMBER)
            return;


        // Calculate the different zones, using the points given
        mPositionChecker.setZoneOne(new RectF(points[0].x,
                                    points[0].y,
                                    points[1].x,
                                    points[1].y + (points[2].y - points[0].y) * PERCENTAGE_OF_SIDE));

        mPositionChecker.setZoneTwo(new RectF(points[0].x,
                mPositionChecker.getZoneOne().bottom + (1.0f - PERCENTAGE_OF_SIDE * 2) * (points[2].y - points[0].y),
                points[3].x,
                points[3].y));

        mZones = new ZoneInfo(new RectF(
                mPositionChecker.getZoneOne().left,
                mPositionChecker.getZoneOne().top,
                mPositionChecker.getZoneTwo().right,
                mPositionChecker.getZoneTwo().bottom),
                HEAT_MAP_ZONES_WIDTH,
                HEAT_MAP_ZONES_HEIGHT);
    }

    public PointF getLastBallCoordinates() {
        return lastBallCoordinates[0];
    }

    public void setLastBallCoordinates(PointF point) {
        if (mBallCoordinates.size() == MAXIMUM_BALL_COORDINATE_NUMBER)
            mBallCoordinates.remove();

        lastBallCoordinates[1] = lastBallCoordinates[0];
        lastBallCoordinates[0] = point;

        mZones.assignValue(point);

        mBallCoordinates.add(point);

        mPositionChecker.onNewFrame(point,
                                    this);

        mCurrentSpeed = mPositionChecker.calculateSpeed(lastBallCoordinates[0],
                                                        lastBallCoordinates[1]);

        if (point != null) {
            mAverageSpeed = ((mAverageSpeed * mAverageSpeedCounter) + mCurrentSpeed) / (mAverageSpeedCounter + 1);
            mAverageSpeedCounter++;
        }

        if (mMaxSpeed < mCurrentSpeed)
            mMaxSpeed = mCurrentSpeed;
    }

    void fireGoalEvent(EGoalEvent eventType) {
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

    public void setRedScore(int score) {
        mRedScore = score;
    }

    public void setBlueScore(int score) {
        mBlueScore = score;
    }

    public int getRedScore() {
        return mRedScore;
    }

    public int getBlueScore() {
        return mBlueScore;
    }

    public void setOnGoalEventListener(OnGoalEventListener listener) {
        mListener = listener;
    }
}
