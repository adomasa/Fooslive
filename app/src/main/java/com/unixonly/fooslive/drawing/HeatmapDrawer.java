package com.unixonly.fooslive.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Size;

import com.unixonly.fooslive.gamecontrol.ZoneInfo;

/**
 * This class is responsible for drawing an informational
 * heatmap using the data collected during the game
 */
public class HeatmapDrawer {
    // TODO: Set value from app.config
    private static int mMaxAlphaValue;

    /**
     * Draws the heatmap, using the data collected during the game
     * @param canvas
     * The canvas, on which the heatmap is drawn
     * @param zones
     * The object, which holds the data collected during the game
     * @return
     * A drawn canvas
     */
    public static Canvas drawZones(Canvas canvas, ZoneInfo zones) {
        Size sizeOfBitmap = new Size(canvas.getWidth(), canvas.getHeight());
        PointF topLeftCorner = new PointF(0,0);

        int max = findMax(zones);

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
        float zoneWidth = sizeOfBitmap.getWidth() / zones.getWidth();
        float zoneHeight = sizeOfBitmap.getHeight() / zones.getHeight();
        float toAddX = 0, toAddY = 0;
        int[][] values = zones.getValues();
        for (int i = 0; i < zones.getHeight(); i ++)
        {
            for (int j = 0; j < zones.getWidth(); j ++)
            {
                paint.setColor(calculateColor(values[i][j], max, colours));

                canvas.drawRect(new RectF(topLeftCorner.x + toAddX,
                                topLeftCorner.y + toAddY,
                                topLeftCorner.x + toAddX + zoneWidth,
                                topLeftCorner.y + toAddY + zoneHeight),
                        paint);
                toAddX += zoneWidth;
            }
            toAddX = 0;
            toAddY += zoneHeight;
        }

        return canvas;
    }

    private static int findMax(ZoneInfo zones) {
        int toReturn = 0;
        int[][] values = zones.getValues();
        for (int i = 0; i < zones.getHeight(); i ++) {
            for (int j = 0; j < zones.getWidth(); j ++) {
                if (toReturn < values[i][j]) toReturn = values[i][j];
            }
        }

        return toReturn;
    }

    /**
     * Calculates a color using for a given value against a max value
     * @param value
     * The integer which we'll calculate the color for
     * @param maxValue
     * This integer is checked against the value. A value near this value represents a hotspot
     * @param colours
     * The predefined colorspace used for heatmap generation
     * @return
     */
    private static int calculateColor(int value, int maxValue, int[] colours) {
        double percentage = value / (double)(maxValue + 1);
        double colorPercentage = 1d / (colours.length - 1);
        double colorBlock = percentage / colorPercentage;
        int which = (int)Math.floor(colorBlock);
        double residue = percentage - which * colorPercentage;
        double percOfColor = residue / colorPercentage;

        int target = colours[which];
        int next = colours[which + 1];

        int redDelta = Color.red(next) - Color.red(target);
        int greenDelta = Color.green(next) - Color.green(target);
        int blueDelta = Color.blue(next) - Color.blue(target);

        int red = Color.red(target) + (int)(redDelta * percOfColor);
        int green = Color.green(target) + (int)(greenDelta * percOfColor);
        int blue = Color.blue(target) + (int)(blueDelta * percOfColor);

        return Color.argb(mMaxAlphaValue, red, green, blue);
    }
}
