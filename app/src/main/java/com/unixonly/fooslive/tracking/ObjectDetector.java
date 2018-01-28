package com.unixonly.fooslive.tracking;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Created by paulius on 1/28/18.
 */

public class ObjectDetector {
    private ColorDetector mDetector;
    /***
     * #TODO
     * Create the GameController class
     */
    // private GameController mGameController;
    private float mMulX;
    private float mMulY;
    private Paint mPaintBall;
    /***
     * #TODO
     * Set value from app.config
     */
    private static float mTraceStrokeWidth;

    /***
     * #TODO
     * Set value from app.config
     */
    private static int mTraceMaxAlpha;

    /***
     * #TODO
     * Set value from app.config
     */
    private static int mTraceDivisor;

    /***
     * #TODO
     * Set value from app.config
     */
    private static int mTraceToAdd;

    /***
     * #TODO
     * Pass GameController to constructor
     */
    public ObjectDetector(float mulX, float mulY, ColorDetector detector) {
        mMulX = mulX;
        mMulY = mulY;
        mDetector = detector;
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
        if (canvas == null || hsv == null || bitmap == null)
            return false;

        boolean ballDetected = false;

        /***
         * #TODO
         * Test this part
         */
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        mDetector.image = image;

        /***
         * The following variables
         *  are for debugging only!
         */
        Rect blob = new Rect();
        Rect blobBox = new Rect();

        ballDetected = mDetector.DetectBall(hsv,blob,blobBox);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (ballDetected) {
            /***
             * #TODO
             * Set coordinates in the GameController class
             */
            // mGameController.setLastBallCoordinates();
        } //else
        // mGameController.setLastBallCoordinates(null);

        Path path = new Path();

        /***
         * #TODO
         * Set the points from GameController
         *  for drawing
         */
        KeyPoint[] points = null;
        int toPaint = 10;
        boolean startSet = false;
        for (int i = points.length - 1; i > 0; i --) {
            if (points[i] == null) {
                toPaint --;

                if (toPaint == 0)
                    break;
                continue;
            }

            if (startSet) {
                if (i < points.length - 1 && points[i+1] != null) {
                    path.quadTo((float)points[i].pt.x, (float)points[i].pt.y,
                            (float)points[i+1].pt.x, (float)points[i+1].pt.y);
                } else
                    path.lineTo((float)points[i].pt.x, (float)points[i].pt.y);

                mPaintBall.setAlpha(mTraceMaxAlpha * (toPaint / mTraceDivisor) + mTraceToAdd);
                canvas.drawPath(path, mPaintBall);
            } else {
                path.moveTo((float)points[i].pt.x, (float)points[i].pt.y);
                startSet = true;
            }

            toPaint --;

            if (toPaint == 0)
                break;
        }

        return ballDetected;
    }
}
