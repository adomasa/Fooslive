package com.unixonly.fooslive.util;

import android.graphics.PointF;

/**
 * Created by paulius on 2/16/18.
 */

public class MeasurementUtils {
    /**
     * Calculates the difference between two points
     * @param one
     * The coordinates of the first point
     * @param two
     * The coordinates of the second point
     * @param mulX
     * Multiply x values by this integer. If there's no need, set it to 1
     * @param mulY
     * Multiply y values by this integer. If there's no need, set it to 1
     * @return
     * The difference between the points
     */
    public static double calculateSpeed(PointF one, PointF two, double mulX, double mulY) {
        double toReturn = 0;

        if (one == null || two == null) toReturn = 0;
        else toReturn = Math.sqrt(
                (one.x * mulX - two.x * mulX) * (one.x * mulX - two.x * mulX) +
                        (one.y * mulY - two.y * mulY) * (one.y * mulY - two.y * mulY));

        return toReturn;
    }
}
