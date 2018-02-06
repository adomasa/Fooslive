package com.unixonly.fooslive.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by paulius on 1/31/18.
 */

public class AlignZones {
    // TODO: Set value from app.config
    private static float mAlignZonesStrokeWidth;

    // TODO: Pass GameController to constructor
    public static Canvas DrawZones(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mAlignZonesStrokeWidth);
        paint.setPathEffect(new DashPathEffect(new float[]{ 30, 20 }, 0));

        Path contour = new Path();

        // TODO: Get multipliers from app.config
        float bottomLeftX = canvas.getWidth();
        float bottomRightX = canvas.getWidth();
        float bottomY = canvas.getHeight();

        float upperBottomLeftX = canvas.getWidth();
        float upperBottomRightX = canvas.getWidth();
        float upperBottomY = canvas.getWidth();

        float lowerTopLeftX = canvas.getWidth();
        float lowerTopRightX = canvas.getWidth();
        float lowerTopY = canvas.getHeight();

        float topLeftX = canvas.getWidth();
        float topRightX = canvas.getWidth();
        float topY = canvas.getHeight();

        // Draw alignment figure
        contour.moveTo(bottomLeftX, bottomY);
        contour.moveTo(upperBottomLeftX, upperBottomY);
        contour.moveTo(lowerTopLeftX, lowerTopY);
        contour.moveTo(topLeftX, topY);
        contour.moveTo(topRightX, topY);
        contour.moveTo(lowerTopRightX, lowerTopY);
        contour.moveTo(upperBottomRightX, upperBottomY);
        contour.moveTo(bottomRightX, bottomY);
        contour.moveTo(bottomLeftX, bottomY);
        canvas.drawPath(contour, paint);

        // TODO: Implement this part once GameController is ported over
//        gameController.SetTable(new[]
//        {
//            new PointF(lowerTopLeftX, topY),
//                    new PointF(lowerTopRightX, topY),
//                    new PointF(bottomLeftX, bottomY),
//                    new PointF(bottomRightX, bottomY)
//        }, ECaptureMode.LIVE);

        return canvas;
    }
}
