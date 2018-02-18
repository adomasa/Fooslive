package com.unixonly.fooslive.util;

import android.graphics.PointF;

public class UnitUtils {
    private static final int CENTIMETERS_IN_A_METER = 100;
    public static int centimetersToMeters(int centimeters) {
        return centimeters * CENTIMETERS_IN_A_METER;
    }
    public static int metersToCentimeters(int meters) {
        return meters / CENTIMETERS_IN_A_METER;
    }

    /**
     * Calculates the difference between two points
     * @param one the coordinates of the first point
     * @param two the coordinates of the second point
     * @param mulX multiply x values by this number. If there's no need, set it to 1
     * @param mulY multiply y values by this number. If there's no need, set it to 1
     * @return the difference between the points
     */
    public static double calculateSpeed(PointF one, PointF two, double mulX, double mulY) {
        if (one == null || two == null) return 0;

        return Math.sqrt(Math.pow((one.x * mulX - two.x * mulX), 2) +
                Math.pow((one.y * mulY - two.y * mulY), 2));
    }
}
