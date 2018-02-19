package com.unixonly.fooslive.utils.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.unixonly.fooslive.game.GameController;

/**
 * This class is responsible for drawing the rough form
 * of the foosball table, so that alignment is easier for
 * the end user
 */
public class TableTemplateDrawer {
    private static final int sAlignZonesStrokeWidth = 4;

    private static final PointF[] multipliers = new PointF[] {
            new PointF(0.25f, 0.9209f),
            new PointF(0.03f, 0.8023f),
            new PointF(0.20f, 0.35f),
            new PointF(0.42f, 0.2994f),
            new PointF(0.58f, 0.2994f),
            new PointF(0.80f, 0.35f),
            new PointF(0.97f, 0.8023f),
            new PointF(0.75f, 0.9209f),
            new PointF(0.25f, 0.9209f)
    };

    /**
     * Draws the alignment figure using the parameters given
     * @param canvas the canvas, on which the alignment figure is drawn
     * @param controller the GameController, which is given the coordinates of the table
     * @return canvas, which holds the drawn alignment figure
     */
    public static Canvas DrawZones(Canvas canvas, GameController controller) {
        Paint paint = new Paint();
        Path contour = new Path();

        setUpPaintStyle(paint);

        // Calculate coordinates fot the contour
        PointF[] contourCoordinates = new PointF[multipliers.length];
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        for (int i = 0; i < multipliers.length; i++) {
            contourCoordinates[i] = new PointF(canvasWidth * multipliers[i].x,
                    canvasHeight * multipliers[i].y);
        }

        moveContour(contour, contourCoordinates);

        canvas.drawPath(contour, paint);

        controller.setTable(new PointF[]{
                contourCoordinates[2], // top left coordinates
                contourCoordinates[4], // top right coordinates
                contourCoordinates[8], // bottom left coordinates
                contourCoordinates[7] // bottom right coordinates
        });

        return canvas;
    }

    private static void setUpPaintStyle(Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(sAlignZonesStrokeWidth);

        // Set up a path effect for our paint
        float[] effectPattern = new float[]{ 30, 20 };
        DashPathEffect paintEffect = new DashPathEffect(effectPattern, 0);
        paint.setPathEffect(paintEffect);
    }

    private static void moveContour(Path contour, PointF[] coordinates) {
        for (PointF point : coordinates) {
            contour.moveTo(point.x, point.y);
        }
    }
}
