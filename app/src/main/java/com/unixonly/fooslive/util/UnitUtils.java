package com.unixonly.fooslive.util;

public class UnitUtils {
    private static final int CENTIMETERS_IN_A_METER = 100;
    public static int centimetersToMeters(int centimeters) {
        return centimeters * CENTIMETERS_IN_A_METER;
    }
    public static int metersToCentimeters(int meters) {
        return meters / CENTIMETERS_IN_A_METER;
    }
}
