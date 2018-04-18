package com.unixonly.fooslive.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.unixonly.fooslive.game.GameController;
import com.unixonly.fooslive.config.ConfigManager;

import java.util.List;

/**
 * Responsible for drawing template over screen which has to be matched with the foosball table,
 * so that alignment is easier for the end user
 */
public class TableTemplateDrawer {
    /**
     * Draws the alignment figure using the parameters given
     * @param canvas the canvas, on which the alignment figure is drawn
     * @param controller the GameController, which is given the coordinates of the table
     * @return canvas, which holds the drawn alignment figure
     */
    public static Canvas drawZones(Canvas canvas, GameController controller) {
        List<List<Float>> multipliers = ConfigManager.getList("template.coordinates");
        List<Integer> recognitionPoints = ConfigManager.getIntList("template.recogPoints");
        Paint paint = new Paint();
        Path contour = new Path();

        setUpPaintStyle(paint);

        // Calculate coordinates fot the contour
        PointF[] contourCoordinates = new PointF[multipliers.size()];
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        for (int i = 0; i < multipliers.size(); i++) {
            //TODO: check whether extraction of list in list works
            contourCoordinates[i] = new PointF(canvasWidth * (multipliers.get(i)).get(0),
                    canvasHeight * (multipliers.get(i)).get(1));
        }

        addContourPoints(contour, contourCoordinates);

        canvas.drawPath(contour, paint);


        //TODO: check whether extraction of recognitionPoints works
        PointF[] tablePoints = new PointF[4];

        for (int i = 0; i < tablePoints.length; i++) {
            tablePoints[i] = contourCoordinates[recognitionPoints.get(i)];
        }

        //TODO: setting point in controller from drawer violates single responsibility principle
        //TODO: move out setTable() out of drawer class
        controller.setTable(tablePoints);

        return canvas;
    }

    private static void setUpPaintStyle(Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ConfigManager.getInt("template.stroke"));

        // Set up a path effect for our paint
        float[] effectPattern = new float[]{ 30, 20 };
        DashPathEffect paintEffect = new DashPathEffect(effectPattern, 0);
        paint.setPathEffect(paintEffect);
    }

    private static void addContourPoints(Path contour, PointF[] coordinates) {
        for (PointF point : coordinates) {
            contour.moveTo(point.x, point.y);
        }
    }
}
