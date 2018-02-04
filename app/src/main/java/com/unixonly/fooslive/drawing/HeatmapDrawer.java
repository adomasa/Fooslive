package com.unixonly.fooslive.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Size;

import com.unixonly.fooslive.gamecontrol.ZoneInfo;

/**
 * Created by paulius on 2/4/18.
 */

public class HeatmapDrawer {
    // TODO: Set value from app.config
    private static int mMaxAlphaValue;
    // TODO: Set value from app.config
    private static int mMaxHue;
    // TODO: Set value from app.config
    private static int mMaxSaturation;
    // TODO: Set value from app.config
    private static int mMaxValue;

    // TODO: Pass ZoneInfo to constructor
    public static Canvas DrawZones(Canvas canvas, ZoneInfo zones ) {
        Size sizeOfBitmap = new Size(canvas.getWidth(), canvas.getHeight());
        PointF topLeftCorner = new PointF(0,0);

        int max = 0;
        // TODO: Implement for cycle once ZoneInfo is implemented

        int[] colours = {
                Color.argb(mMaxAlphaValue, 0, 0, 0) ,
                Color.argb(mMaxAlphaValue, 0, 0, 0xFF) ,
                Color.argb(mMaxAlphaValue, 0, 0xFF, 0xFF) ,
                Color.argb(mMaxAlphaValue, 0, 0xFF, 0) ,
                Color.argb(mMaxAlphaValue, 0xFF, 0xFF, 0) ,
                Color.argb(mMaxAlphaValue, 0xFF, 0, 0) ,
                Color.argb(mMaxAlphaValue, 0xFF, 0xFF, 0xFF)
        };

        Paint paint = new Paint();
        // TODO: Implement this part once ZoneInfo is implemented
        float zoneWidth = sizeOfBitmap.getWidth();
        float zoneHeight = sizeOfBitmap.getHeight();
        float toAddX = 0, toAddY = 0;
        // TODO: Implement for cycle once ZoneInfo is implemented

        return canvas;
    }
}
