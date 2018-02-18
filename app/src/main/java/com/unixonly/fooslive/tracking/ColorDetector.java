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
        Scalar lowerLimit = calculateBound(hsv, -1);
        Scalar upperLimit = calculateBound(hsv, 1);

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
            if (blob.size < mMinAllowed) continue;
            if (blob.size > mMaxAllowed) continue;
            if (!(mBox.contains((float)blob.pt.x, (float)blob.pt.y))) continue;
            else {
                if (mMinAllowed == 0) mMinAllowed = (int)blob.size;
                else
                    if (mMinAllowed > blob.size) mMinAllowed = (int)blob.size - mMinAddition;

                if (mMaxAllowed < blob.size) mMaxAllowed = (int)blob.size + mMaxAddition;

                mLastSize = (int)blob.size;
            }

            biggestBlob = blob;
            mFramesLost = 0;
            mLastBlob = new PointF((float)blob.pt.x, (float)blob.pt.y);
            break;
        }

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

    private Scalar calculateBound(Scalar hsv, int boundIndicator) {
        double lowerHue = hsv.val[0] + (Threshold / (double)mHsvDivisor) * boundIndicator;
        double lowerSaturation = hsv.val[1] + (Threshold * mSaturationMultiplier) * boundIndicator;
        double lowerValue = hsv.val[2] + (Threshold * mValueMultiplier) * boundIndicator;

        return new Scalar(lowerHue, lowerSaturation, lowerValue);
    }

    private void updateBox(KeyPoint blob) {
        if (blob == null) return;
        if (mLastBlob == null) return;

        float toAddX = mLastBlob.x - (float)blob.pt.x;
        float toAddY = mLastBlob.y - (float)blob.pt.y;

        if (toAddX < 0) toAddX *= -1;
        if (toAddY < 0) toAddY *= -1;

        float addX, addY;

        if (blob.size > mMinBlobSize) {
            addX = blob.size * mMulDeltaWidth + toAddX * mMulDeltaX;
            addY = blob.size * mMulDeltaHeight + toAddY * mMulDeltaY;
        } else {
            addX = mMinWidth + toAddX * mMulDeltaX;
            addY = mMinHeight + toAddY * mMulDeltaY;
        }

        addX /= 2;
        addY /= 2;

        mBox = new RectF((float)blob.pt.x - addX,
                        (float)blob.pt.y - addY,
                        (float)blob.pt.x + addX,
                        (float)blob.pt.y + addY);
    }
}
