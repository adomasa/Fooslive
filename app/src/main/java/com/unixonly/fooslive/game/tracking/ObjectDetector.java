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

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ObjectDetector {
    private ColorDetector mDetector;
    private GameController mGameController;
    private float mMulX;
    private float mMulY;
    private Paint mPaintBall;

    // TODO: Move value to app.config
    private static int mTraceStrokeWidth = 16;
    // TODO: Set value from app.config
    private static int mTraceMaxAlpha;
    // TODO: Set value from app.config
    private static int mTraceDivisor;
    // TODO: Set value from app.config
    private static int mTraceToAdd;

    public ObjectDetector(PointF multipliers, ColorDetector detector, GameController controller) {
        mMulX = multipliers.x;
        mMulY = multipliers.y;
        mDetector = detector;
        mGameController = controller;
    }

    public void setColor(Scalar hsv) {
        mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBall.setColor(Color.HSVToColor(
                new float[] {(float)hsv.val[0] * 2f, (float)hsv.val[1] / 255,
                        (float)hsv.val[2] / 255
                }
        ));
        mPaintBall.setStyle(Paint.Style.STROKE);
        mPaintBall.setStrokeWidth(mTraceStrokeWidth);
    }

    //TODO add javadoc

    /**
     * Detects the ball using an HSV value and paints it's trace to a given canvas
     * @param canvas The canvas, on which the trace is drawn
     * @param hsv The HSV value of the ball
     * @param bitmap The alpha bitmap, used to clear the canvas
     * @return True if a ball was detected. False otherwise
     */
    public boolean detect(Canvas canvas, Scalar hsv, Bitmap bitmap) {
        if (canvas == null || hsv == null || bitmap == null) return false;

        boolean ballDetected = false;

        // TODO: Test this part
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        mDetector.image = image;

        // The following variables are for debugging only!
        Rect blob = new Rect();
        Rect blobBox = new Rect();

        ballDetected = mDetector.detectBall(hsv,blob,blobBox);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (ballDetected) {
            mGameController.setLastBallCoordinates(new PointF(blob.centerX(), blob.centerY()));
        } else mGameController.setLastBallCoordinates(null);

        drawBallTrace(canvas);

        return ballDetected;
    }

    private void drawBallTrace(Canvas canvas) {
        Path path = new Path();
        PointF[] points = (PointF[]) mGameController.getBallCoordinates().toArray();
        // TODO: Move this variable to a config file
        int toPaint = 10;
        boolean startSet = false;

        for (int i = points.length - 1; i > 0 && toPaint != 0; i--, toPaint--) {
            if (points[i] == null) continue;

            if (startSet) {
                movePath(path, points, i);
                mPaintBall.setAlpha(mTraceMaxAlpha * (toPaint / mTraceDivisor) + mTraceToAdd);
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
