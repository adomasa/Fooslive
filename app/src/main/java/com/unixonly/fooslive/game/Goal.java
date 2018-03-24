package com.unixonly.fooslive.game;

import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.unixonly.fooslive.game.model.Team;
import com.unixonly.fooslive.utils.ConfigManager;

import java.util.Iterator;
import java.util.Queue;

import static com.unixonly.fooslive.utils.UnitUtils.calculateSpeed;
import static com.unixonly.fooslive.utils.UnitUtils.metersToCentimeters;

/**
 * Contains the data related to a particular goal, which occurred during the game
 */
public class Goal {
    private PointF[] mPoints;

    private final double[] mSpeeds;
    private double mMaxSpeed;

    private final @Team.Type int team;

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
        double tableWidth = ConfigManager.getDouble("game.width_table");
        double tableHeight = ConfigManager.getDouble("game.height_table");

        mPoints = new PointF[points.size()];
        mSpeeds = new double[points.size()];
        mMaxSpeed = 0;

        int i = 0;
        Iterator<PointF> iterator = points.iterator();
        for (; iterator.hasNext(); i++) mPoints[i] = iterator.next();

        double mulX = tableWidth / (tablePoints.right - tablePoints.left);
        double mulY = tableHeight / (tablePoints.bottom - tablePoints.top);

        mulX *= metersToCentimeters(1);
        mulY *= metersToCentimeters(1);

        this.team = team;

        calculateSpeedArray(mulX, mulY);
    }

    /**
     * Calculates speeds for all points preceding the goal
     * @param mulX
     * The x upscaling constant
     * @param mulY
     * The y upscaling constant
     */
    private void calculateSpeedArray(double mulX, double mulY) {
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

    public double[] getSpeedArray() {
        return mSpeeds;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public long getDuration() {
        return mDuration;
    }
}
