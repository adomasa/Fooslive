package com.unixonly.fooslive.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.unixonly.fooslive.gamecontrol.GameController;

/**
 * Created by paulius on 1/31/18.
 */

/**
 * This class is responsible for drawing the rough form
 * of the foosball table, so that alignment is easier for
 * the end user
 */
public class TableTemplateDrawer {
    private static final int sAlignZonesStrokeWidth = 4;

    private static final float sBottomLeftX = 0.25f;
    private static final float sBottomRightX = 0.75f;
    private static final float sBottomY = 0.9209f;
    private static final float sUpperBottomLeftX = 0.03f;
    private static final float sUpperBottomRightX = 0.97f;
    private static final float sUpperBottomY = 0.8023f;
    private static final float sLowerTopLeftX = 0.20f;
    private static final float sLowerTopRightX = 0.80f;
    private static final float sLowerTopY = 0.35f;
    private static final float sTopLeftX = 0.42f;
    private static final float sTopRightX = 0.58f;
    private static final float sTopY = 0.2994f;

    /**
     * Draws the alignment figure using the parameters given
     * @param canvas
     * The canvas, on which the alignment figure is drawn
     * @param controller
     * The GameController, which is given the coordinates of the table
     * @return
     * Canvas, which holds the drawn alignment figure
     */
    public static Canvas DrawZones(Canvas canvas, GameController controller) {
        Paint paint = new Paint();
        Path contour = new Path();

        setUpPaintStyle(paint);

        moveContour(contour);

        canvas.drawPath(contour, paint);

        controller.setTable(new PointF[]{
                new PointF(sLowerTopLeftX, sTopY),
                new PointF(sLowerTopRightX, sTopY),
                new PointF(sBottomLeftX, sBottomY),
                new PointF(sBottomRightX, sBottomY)
        });

        return canvas;
    }

    private static void setUpPaintStyle(Paint paint) {
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(sAlignZonesStrokeWidth);
        paint.setPathEffect(new DashPathEffect(new float[]{ 30, 20 }, 0));
    }

    private static void moveContour(Path contour) {
        contour.moveTo(sBottomLeftX, sBottomY);
        contour.moveTo(sUpperBottomLeftX, sUpperBottomY);
        contour.moveTo(sLowerTopLeftX, sLowerTopY);
        contour.moveTo(sTopLeftX, sTopY);
        contour.moveTo(sTopRightX, sTopY);
        contour.moveTo(sLowerTopRightX, sLowerTopY);
        contour.moveTo(sUpperBottomRightX, sUpperBottomY);
        contour.moveTo(sBottomRightX, sBottomY);
        contour.moveTo(sBottomLeftX, sBottomY);
    }
}
