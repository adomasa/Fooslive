package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

public class ZoneInfo {
    // TODO: Move these values to app.config
    private static int[] sZoneMultipliers = new int[] {8, 4, 2};

    private int[][] mValues;
    private int mHeight;
    private int mWidth;

    private float mZoneWidth;
    private float mZoneHeight;
    private float mTopLeftX;
    private float mTopLeftY;

    public ZoneInfo(RectF tableInfo, int height, int width) {
        mValues = new int[height][width];
        mWidth = width;
        mHeight = height;
        mZoneHeight = ((tableInfo.bottom - tableInfo.top) / width);
        mZoneWidth = ((tableInfo.right - tableInfo.left) / height);
        mTopLeftX = tableInfo.left;
        mTopLeftY = tableInfo.top;
    }

    //TODO: add javadoc

    /**
     * Assigns a value to a given point for the heatmap
     * @param point the center point
     */
    public void assignValue(PointF point) {
        if (point == null) return;

        float x = point.x - mTopLeftX;
        float y = point.y - mTopLeftY;

        int posX = (int) (x / mZoneWidth);
        int posY = (int) (y / mZoneHeight);

        if (!(posX < mWidth && posY < mHeight) || !(posX >= 0 && posY >= 0)) return;

        mValues[posY][posX] += 8;

        /* We currently assign values to 26 points surrounding
         * the position to make the heatmap prettier
         */
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                addToZone(posX, posY, i, j);
            }
        }
    }

    private void addToZone(int posX, int posY, int indexWidth, int indexHeight) {
        if (posX + indexWidth > mWidth || posX + indexWidth < 0 || posY + indexHeight > mHeight || posY + indexHeight < 0) return;

        // |indexWidth| and |indexHeight| == 2 defines the outermost points from the center
        // |indexWidth| and |indexHeight| == 1 defines the points, which surround the center point
        // |indexWidth| and |indexHeight| == 0 defines the center point
        for (int k = 2; k >= 0; k--) {
            if (Math.abs(indexWidth) == k && Math.abs(indexHeight) == k) {
                mValues[posY + indexHeight][posX + indexWidth] += sZoneMultipliers[k];
                return;
            }
        }
    }

    public int[][] getValues() {
        return mValues;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
