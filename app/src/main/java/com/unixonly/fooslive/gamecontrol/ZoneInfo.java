package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

public class ZoneInfo {
    // TODO: Move these values to app.config
    private static int[] sZoneMultipliers = new int[] {
            8,
            4,
            2
    };

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

    public void assignValue(PointF point) {
        if (point == null) return;

        float x = point.x - mTopLeftX;
        float y = point.y - mTopLeftY;

        int posX = (int) (x / mZoneWidth);
        int posY = (int) (y / mZoneHeight);

        if (!(posX < mWidth && posY < mHeight)) return;
        if (!(posX >= 0 && posY >= 0)) return;

        mValues[posY][posX] += 8;

        /**
         * We currently assign values to 26 points surrounding
         *  the position to make the heatmap prettier
         */
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                addToZone(posX, posY, i, j);
            }
        }
}

    private void addToZone(int posX, int posY, int i, int j) {
        if (posX + i > mWidth) return;
        if (posX + i < 0) return;
        if (posY + j > mHeight) return;
        if (posY + j < 0) return;

        // Defines the outermost points from the center
        if ((i == -2 || i == 2) && (j == -2 || j == 2)) {
            mValues[posY + j][posX + i] += sZoneMultipliers[2];
            return;
        }

        // Defines the points, which surround the center point
        if ((i == -1 || i == 1) && (j == -1 || j == 1)) {
            mValues[posY + j][posX + i] += sZoneMultipliers[1];
            return;
        }

        // Defines the center point
        mValues[posY + j][posX + i] += sZoneMultipliers[0];
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
