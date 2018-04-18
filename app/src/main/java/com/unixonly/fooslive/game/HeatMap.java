package com.unixonly.fooslive.game;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.config.ConfigManager;

import java.util.List;

public class HeatMap {
    private final List<Integer> mZonesMultipliers;
    private final int mHeatMapWidth;
    private final int mHeatMapHeight;

    public float getZoneWidth() {
        return mZoneWidth;
    }

    public float getZoneHeight() {
        return mZoneHeight;
    }

    private final float mZoneWidth;
    private final float mZoneHeight;
    private final float mTopLeftX;
    private final float mTopLeftY;

    private int[][] mValues;

    public HeatMap(RectF tableInfo) {
        mZonesMultipliers = ConfigManager.getIntList("multipliers.zones");
        mHeatMapHeight = ConfigManager.getInt("heatmap.pts_width");
        mHeatMapWidth = ConfigManager.getInt("heatmap.pts_height");

        mValues = new int[mHeatMapHeight][mHeatMapWidth];
        mZoneHeight = ((tableInfo.bottom - tableInfo.top) / mHeatMapWidth);
        mZoneWidth = ((tableInfo.right - tableInfo.left) / mHeatMapHeight);
        mTopLeftX = tableInfo.left;
        mTopLeftY = tableInfo.top;
    }

    /**
     * Assign a value to a given point for the heat map
     * @param point the center point
     */
    public void setValue(PointF point) {
        if (point == null) return;

        float x = point.x - mTopLeftX;
        float y = point.y - mTopLeftY;

        int posX = (int) (x / mZoneWidth);
        int posY = (int) (y / mZoneHeight);

        if (!(posX < mHeatMapHeight && posY < mHeatMapWidth) || !(posX >= 0 && posY >= 0)) return;

        mValues[posY][posX] += 8;

        /* We currently assign values to 26 points surrounding
         * the position to make the heat map prettier
         */
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                addToZone(posX, posY, i, j);
            }
        }
    }

    private void addToZone(int posX, int posY, int indexWidth, int indexHeight) {
        if (posX + indexWidth > mHeatMapHeight || posX + indexWidth < 0 ||
                posY + indexHeight > mHeatMapWidth || posY + indexHeight < 0) return;

        // |indexWidth| and |indexHeight| == 2 defines the outermost points from the center
        // |indexWidth| and |indexHeight| == 1 defines the points, which surround the center point
        // |indexWidth| and |indexHeight| == 0 defines the center point
        for (int k = 2; k >= 0; k--) {
            if (Math.abs(indexWidth) == k && Math.abs(indexHeight) == k) {
                mValues[posY + indexHeight][posX + indexWidth] += mZonesMultipliers.get(k);
                return;
            }
        }
    }

    public int[][] getData() {
        return mValues;
    }


}
