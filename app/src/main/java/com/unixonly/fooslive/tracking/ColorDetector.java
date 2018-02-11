package com.unixonly.fooslive.tracking;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by paulius on 1/27/18.
 */

/**
 * This class is responsible for detecting a foosball ball
 * from a given image
 */
public class ColorDetector {
    public static String TAG = ColorDetector.class.toString();

    // TODO: Set value from app.config
    private static int sDefaultThreshold;

    private boolean mBoxSet = false;
    private RectF mBox;
    // TODO: Set value from app.config
    private int mBoxWidth;
    // TODO: Set value from app.config
    private int mBoxHeight;
    private int mFramesLost = 0;
    // TODO: Set value from app.config
    private int mFramesLostToNewBoundingBox;
    private PointF mLastBlob;
    private int mLastSize = 0;

    // TODO: Set value from app.config
    private int mMinBlobSize;
    // TODO: Set value from app.config
    private int mHsvDivisor;
    // TODO: Move value to app.config
    private float mSaturationMultiplier = 1.3f;
    // TODO: Move value to app.config
    private float mValueMultiplier = 1.3f;

    // TODO: Set value from app.config
    private int mMulDeltaX;
    // TODO: Set value from app.config
    private int mMulDeltaY;
    // TODO: Set value from app.config
    private int mMulDeltaWidth;
    // TODO: Set value from app.config
    private int mMulDeltaHeight;
    // TODO: Set value from app.config
    private int mMinWidth;
    // TODO: Set value from app.config
    private int mMinHeight;

    // TODO: Move value to app.config
    private int mMinAddition = 5;
    // TODO: Move value to app.config
    private int mMaxAddition = 5;

    public Mat image;
    public int Threshold;

    private BlobDetector mDetector;

    private int mMinAllowed;
    private int mMaxAllowed;

    public ColorDetector() {
        Threshold = sDefaultThreshold;
        mBox = new RectF();
        mDetector = new BlobDetector();
    }

    /**
     * Detects a ball from a given image
     * @param hsv
     * The hsv values of the ball, which is to be detected
     * @param rect
     * Defines the rectangle of the blob
     * @param blobBox
     * Defines the area in which the algorithm searches for the ball
     * @return
     * True if a ball is found. False otherwise
     */
    public boolean detectBall(Scalar hsv, Rect rect, Rect blobBox) {
        // Convert RGB to HSV
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV);

        // Calculate the lower and upper bounds
        Scalar lowerLimit = calculateLowerBound(hsv);
        Scalar upperLimit = calculateUpperBound(hsv);

        // Filter the hsv image by color
        Core.inRange(image, lowerLimit, upperLimit, image);

        MatOfKeyPoint blobs = new MatOfKeyPoint();
        mDetector.getBlobs(image, blobs);

        if ( mFramesLost > mFramesLostToNewBoundingBox || !mBoxSet) {
            mBox = new RectF(((float)image.size().width- mBoxWidth) / 2,
                    ((float)image.size().height - mBoxHeight) / 2,
                    ((float)image.size().width + mBoxWidth) / 2,
                    ((float)image.size().height + mBoxHeight) / 2);
            mFramesLost = 0;
            mBoxSet = true;
        }

        image = null;

        if (blobs.size().empty()) {
            mFramesLost++;
            return false;
        }

        KeyPoint biggestBlob = null;
        for (KeyPoint blob : blobs.toArray()) {
            if (mBox.contains((float)blob.pt.x, (float)blob.pt.y) ) {
                if (mMinAllowed == 0)
                    mMinAllowed = (int)blob.size;
                else
                    if (mMinAllowed > blob.size)
                        mMinAllowed = (int)blob.size - mMinAddition;

                if (mMaxAllowed < blob.size)
                    mMaxAllowed = (int)blob.size + mMaxAddition;

                biggestBlob = blob;
                mFramesLost = 0;
                mLastSize = (int)blob.size;
                break;
            } else
                if (blob.size > mMinAllowed && blob.size < mMaxAllowed) {
                biggestBlob = blob;
                mFramesLost = 0;
                mLastBlob = new PointF((float)blob.pt.x, (float)blob.pt.y);
                break;
                }
        }

        mLastBlob = biggestBlob == null ? null : new PointF((float)biggestBlob.pt.x, (float)biggestBlob.pt.y);

        updateBox(biggestBlob);

        if (biggestBlob != null) {
            rect.left = (int)biggestBlob.pt.x - (int)(biggestBlob.size / 2);
            rect.top = (int)biggestBlob.pt.y - (int)(biggestBlob.size / 2);
            rect.right = (int)biggestBlob.pt.x + (int)(biggestBlob.size / 2);
            rect.bottom = (int)biggestBlob.pt.y - (int)(biggestBlob.size / 2);
            return true;
        } else {
            mFramesLost ++;
            return false;
        }
    }

    private Scalar calculateLowerBound(Scalar hsv) {
        double lowerHue = hsv.val[0] - Threshold / (double)mHsvDivisor;
        double lowerSaturation = hsv.val[1] - Threshold * mSaturationMultiplier;
        double lowerValue = hsv.val[2] - Threshold * mValueMultiplier;

        return new Scalar(lowerHue, lowerSaturation, lowerValue);
    }

    private Scalar calculateUpperBound(Scalar hsv) {
        double upperHue = hsv.val[0] + Threshold / (double)mHsvDivisor;
        double upperSaturation = hsv.val[1] + Threshold * mSaturationMultiplier;
        double upperValue = hsv.val[2] + Threshold * mValueMultiplier;

        return new Scalar(upperHue, upperSaturation, upperValue);
    }

    private void updateBox(KeyPoint blob) {
        if (blob == null)
            return;

        float toAddX, toAddY;
        toAddX = mLastBlob.x - (float)blob.pt.x;
        toAddY = mLastBlob.y - (float)blob.pt.y;

        if (toAddX < 0)
            toAddX *= -1;
        if (toAddY < 0)
            toAddY *= -1;

        if (blob.size > mMinBlobSize) {
            mBox = new RectF((float)blob.pt.x - (blob.size * mMulDeltaWidth + toAddX * mMulDeltaX) / 2,
                    (float)blob.pt.y - (blob.size * mMulDeltaHeight + toAddY * mMulDeltaY) / 2,
                    (float)blob.pt.x + (blob.size * mMulDeltaWidth + toAddX * mMulDeltaX) / 2,
                    (float)blob.pt.y + (blob.size * mMulDeltaWidth + toAddX * mMulDeltaX) / 2);
        } else {
            mBox = new RectF((float)blob.pt.x - (mMinWidth + toAddX * mMulDeltaX) / 2,
                    (float)blob.pt.y - (mMinHeight + toAddX * mMulDeltaX) / 2,
                    (float)blob.pt.x + (mMinWidth + toAddX * mMulDeltaX) / 2,
                    (float)blob.pt.y + (mMinHeight + toAddX * mMulDeltaX) / 2);
        }
    }
}
