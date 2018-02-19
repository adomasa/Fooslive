package com.unixonly.fooslive.game;

import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.unixonly.fooslive.game.model.Team;

import java.util.Iterator;
import java.util.Queue;

import static com.unixonly.fooslive.utils.UnitUtils.calculateSpeed;
import static com.unixonly.fooslive.utils.UnitUtils.metersToCentimeters;

/**
 * Contains the data related to a particular goal, which
 * occurred during the game
 */
public class Goal {

    // TODO: Set value from app.config
    public static double sRealTableWidth;
    // TODO: Set value from app.config
    public static double sRealTableHeight;

    private PointF[] mPoints;

    private final double[] mSpeeds;
    private double mMaxSpeed;

    // Which team scored: true if blue, false otherwise
    private @Team.Type
    int mTeamColor;

    private long mDuration;

    /**
     * TODO: Add start and end timestamps when a timer is implemented
     * @param points
     * The coordinates of the ball before the goal
     * @param tablePoints
     * The coordinates of the table corners. These are used for speed calculation
     * @param team
     * Which team scored
     */
    public Goal(@NonNull Queue<PointF> points, @NonNull RectF tablePoints, @Team.Type int team) {
        mPoints = new PointF[points.size()];
        mSpeeds = new double[points.size()];
        mMaxSpeed = 0;

        int i = 0;
        Iterator<PointF> iterator = points.iterator();
        for (; iterator.hasNext(); i++) mPoints[i] = iterator.next();

        double mulX = sRealTableWidth / (tablePoints.right - tablePoints.left);
        double mulY = sRealTableHeight / (tablePoints.bottom - tablePoints.top);

        mulX *= metersToCentimeters(1);
        mulY *= metersToCentimeters(1);

        calculateSpeeds(mulX, mulY);
    }

    /**
     * Calculates speeds for all points preceding the goal
     * @param mulX
     * The x upscaling constant
     * @param mulY
     * The y upscaling constant
     */
    private void calculateSpeeds(double mulX, double mulY) {
        PointF lastPoint = null;
        int lostFrameCount = 0;
        int i = 0;

        for (PointF point : mPoints) {
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }

            if (point == null) {
                lostFrameCount++;
                i++;
                continue;
            }

            mSpeeds[i] = calculateSpeed(point, lastPoint, mulX, mulY) / (lostFrameCount + 1.0f);

            if (mMaxSpeed < mSpeeds[i]) mMaxSpeed = mSpeeds[i];

            i++;
            lastPoint = point;
            lostFrameCount = 0;
        }
    }

    public double[] getSpeeds() {
        return mSpeeds;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public long getDuration() {
        return mDuration;
    }
}
