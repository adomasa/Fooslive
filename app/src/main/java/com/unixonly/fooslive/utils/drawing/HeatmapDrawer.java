package com.unixonly.fooslive.utils.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Size;

import com.unixonly.fooslive.game.HeatMap;
import com.unixonly.fooslive.utils.ConfigManager;

/**
 * This class is responsible for drawing an informational
 * heatmap using the data collected during the game
 */
public class HeatmapDrawer {
    private static int mMaxAlpha;

    /**
     * Draws the heatmap, using the data collected during the game
     * @param canvas canvas, on which the heatmap is drawn
     * @param heatMap the object, which holds the data collected during the game
     * @return drawn canvas
     */
    public static Canvas drawZones(Canvas canvas, HeatMap heatMap) {
        mMaxAlpha = ConfigManager.getInt("heatmap.max_zone_alpha");

        Size sizeOfBitmap = new Size(canvas.getWidth(), canvas.getHeight());
        PointF topLeftCorner = new PointF(0,0);

        int max = findMax(heatMap);

        int[] colours = {
                Color.argb(mMaxAlpha, 0, 0, 0) ,
                Color.argb(mMaxAlpha, 0, 0, 0xFF) ,
                Color.argb(mMaxAlpha, 0, 0xFF, 0xFF) ,
                Color.argb(mMaxAlpha, 0, 0xFF, 0) ,
                Color.argb(mMaxAlpha, 0xFF, 0xFF, 0) ,
                Color.argb(mMaxAlpha, 0xFF, 0, 0) ,
                Color.argb(mMaxAlpha, 0xFF, 0xFF, 0xFF)
        };

        Paint paint = new Paint();
        float zoneWidth = sizeOfBitmap.getWidth() / heatMap.getZoneWidth();
        float zoneHeight = sizeOfBitmap.getHeight() / heatMap.getZoneHeight();
        float toAddX = 0;
        float toAddY = 0;
        int[][] values = heatMap.getData();
        for (int i = 0; i < heatMap.getZoneHeight(); i ++) {
            for (int j = 0; j < heatMap.getZoneWidth(); j ++) {
                paint.setColor(processColor(values[i][j], max, colours));

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

    private static int findMax(HeatMap zones) {
        int toReturn = 0;
        int[][] values = zones.getData();
        for (int i = 0; i < zones.getZoneHeight(); i ++) {
            for (int j = 0; j < zones.getZoneWidth(); j ++) {
                if (toReturn < values[i][j]) toReturn = values[i][j];
            }
        }

        return toReturn;
    }

    /**
     * Calculates a color using for a given value against a max value
     * @param value value for color calculus
     * @param maxValue represents a hotspot. maxValue is checked against the value
     * @param colours the predefined colorspace used for heatmap generation
     * @return argb color value
     */
    private static int processColor(int value, int maxValue, int[] colours) {
        double percentage = value / (double)(maxValue + 1);
        /*
         * Defines the zone a specific color occupies in a heatmap
         * For example, a heatmap colorspace consisting of 4 different colors.
         * Each color occupies 25 % of each individual range
         */
        double colorPercentage = 1d / (colours.length - 1);
        double colorBlock = percentage / colorPercentage;
        int which = (int)Math.floor(colorBlock);
        double residue = percentage - which * colorPercentage;
        double percentageOfColor = residue / colorPercentage;

        int target = colours[which];
        int next = colours[which + 1];

        int redDelta = Color.red(next) - Color.red(target);
        int greenDelta = Color.green(next) - Color.green(target);
        int blueDelta = Color.blue(next) - Color.blue(target);

        int red = Color.red(target) + (int)(redDelta * percentageOfColor);
        int green = Color.green(target) + (int)(greenDelta * percentageOfColor);
        int blue = Color.blue(target) + (int)(blueDelta * percentageOfColor);

        return Color.argb(mMaxAlpha, red, green, blue);
    }
}
