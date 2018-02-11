package com.unixonly.fooslive.tracking;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.unixonly.fooslive.gamecontrol.GameController;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Created by paulius on 1/28/18.
 */

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

    public ObjectDetector(float mulX,
                          float mulY,
                          ColorDetector detector,
                          GameController controller) {
        mMulX = mulX;
        mMulY = mulY;
        mDetector = detector;
        mGameController = controller;
    }

    public void setColor(Scalar hsv) {
        mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBall.setColor(Color.HSVToColor(
                new float[] {
                        (float)hsv.val[0] * 2,
                        (float)hsv.val[1] / 255,
                        (float)hsv.val[2] / 255
                }
        ));
        mPaintBall.setStyle(Paint.Style.STROKE);
        mPaintBall.setStrokeWidth(mTraceStrokeWidth);
    }

    public boolean detect(Canvas canvas, Scalar hsv, Bitmap bitmap) {
        if (canvas == null || hsv == null || bitmap == null) return false;

        boolean ballDetected = false;

        // TODO: Test this part
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        mDetector.image = image;

        /***
         * The following variables
         *  are for debugging only!
         */
        Rect blob = new Rect();
        Rect blobBox = new Rect();

        ballDetected = mDetector.detectBall(hsv,blob,blobBox);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (ballDetected) {
            mGameController.setLastBallCoordinates(new PointF(blob.centerX(),
                                                                blob.centerY()));
        } else mGameController.setLastBallCoordinates(null);

        drawBallTrace(canvas);

        return ballDetected;
    }

    private void drawBallTrace(Canvas canvas) {
        Path path = new Path();
        PointF[] points = (PointF[])mGameController.getBallCoordinates().toArray();
        int toPaint = 10;
        boolean startSet = false;
        for (int i = points.length - 1; i > 0; i --) {
            if (points[i] == null) {
                toPaint --;

                if (toPaint == 0) break;
                else continue;
            }

            if (startSet) {
                if (i < points.length - 1 && points[i+1] != null) {
                    path.quadTo(points[i].x, points[i].y,
                            points[i+1].x, points[i+1].y);
                } else path.lineTo(points[i].x, points[i].y);

                mPaintBall.setAlpha(mTraceMaxAlpha * (toPaint / mTraceDivisor) + mTraceToAdd);
                canvas.drawPath(path, mPaintBall);
            } else {
                path.moveTo(points[i].x, points[i].y);
                startSet = true;
            }

            toPaint --;

            if (toPaint == 0) break;
        }
    }
}
