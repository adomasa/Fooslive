package com.unixonly.fooslive.game.tracking;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.unixonly.fooslive.game.GameController;
import com.unixonly.fooslive.utils.ConfigManager;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ObjectDetector {
    // The main detector, which is used for detecting a ball from a given image
    private ColorDetector mDetector;
    // The class, which is responsible for score keeping and oversees game related events
    private GameController mGameController;

    // This paint is used for painting the ball trace, allowing for easier tracking
    private Paint mPaintBall;

    // Used for upscaling the ball coordinates
    private PointF mUpscalingMultipliers;

    // Used in painting the ball trace, it defines the width
    private final int traceStrokeWidth;
    // Used in painting the ball trace, it defines the preliminary alpha value
    private final int traceMaxAlpha;

    // Defines how much an alpha value decreases for every historic point away from the ball
    private final int traceDivisor;
    // Defines how much we compensate for the decreased alpha value
    private final int traceToAdd;
    // Defines how much historic points we paint for the ball trace
    private final int toPaint;

    private Scalar mBallHSV;

    public ObjectDetector(ColorDetector detector, GameController controller) {
        traceStrokeWidth = ConfigManager.getInt("trace.stroke_rect");
        traceMaxAlpha = ConfigManager.getInt("trace.max_alpha");
        traceDivisor = ConfigManager.getInt("trace.divisor");
        traceToAdd = ConfigManager.getInt("trace.to_add");
        toPaint = ConfigManager.getInt("trace.to_paint");

        mDetector = detector;
        mGameController = controller;

        setUpPaintBall();
    }

    public void setUpscalingMultipliers(PointF multipliers) {
        mUpscalingMultipliers = multipliers;
    }

    public void setHsvColor(Scalar hsv) {
        mPaintBall.setColor(Color.HSVToColor(new float[] {
                (float)hsv.val[0] * 2f,
                (float)hsv.val[1] / 255,
                (float)hsv.val[2] / 255
        }));
        mBallHSV = hsv;
    }

    private void setUpPaintBall() {
        mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBall.setStyle(Paint.Style.STROKE);
        mPaintBall.setStrokeWidth(traceStrokeWidth);
    }

    //TODO add javadoc

    /**
     * Detects the ball using an HSV value and paints it's trace to a given canvas
     * @param canvas The canvas, on which the trace is drawn
     * @param hsv The HSV value of the ball
     * @param bitmap The alpha bitmap, used to clear the canvas
     * @return True if a ball was detected. False otherwise
     */
    public boolean detect(Canvas canvas, Bitmap bitmap) {
        if (canvas == null || mBallHSV == null || bitmap == null) return false;

        boolean ballDetected = false;

        // TODO: Test this part
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);

        // The following variables are for debugging only!
        Rect blob = new Rect();

        ballDetected = mDetector.detectBallFromImage(mBallHSV,blob,image);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (ballDetected) {
            mGameController.setLastBallCoordinates(new PointF(blob.centerX(), blob.centerY()));
        } else mGameController.setLastBallCoordinates(null);

        drawBallTrace(canvas);

        return ballDetected;
    }

    private void drawBallTrace(Canvas canvas) {
        Path path = new Path();
        PointF[] points = mGameController.getBallCoordinates().toArray(new PointF[0]);
        int ptsToDraw = toPaint;
        boolean startSet = false;

        for (int i = points.length - 1; i > 0 && toPaint != 0; i--, ptsToDraw--) {
            if (points[i] == null) continue;

            if (startSet) {
                movePath(path, points, i);
                mPaintBall.setAlpha(traceMaxAlpha * (toPaint / traceDivisor) + traceToAdd);
                canvas.drawPath(path, mPaintBall);
            } else {
                path.moveTo(points[i].x, points[i].y);
                startSet = true;
            }
        }
    }

    private void movePath(Path path, PointF[] points, int i) {
        if (i < points.length - 1 && points[i+1] != null) {
            path.quadTo(points[i].x, points[i].y, points[i+1].x, points[i+1].y);
        } else path.lineTo(points[i].x, points[i].y);
    }
}
