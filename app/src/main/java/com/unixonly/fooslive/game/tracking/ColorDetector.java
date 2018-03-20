package com.unixonly.fooslive.game.tracking;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.unixonly.fooslive.utils.ConfigManager;

import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

/**
 * Responsible for detecting the ball from a given image
 */
public class ColorDetector {
    public static String TAG = "ColorDetector";

    // Tells whether to reset the bounding box or not
    private boolean mBoxSet = false;
    // The bounding box, which defines the area in which we search for the ball
    private RectF mBox;

    // Defines the frame count, from which we reset the bounding box
    private final int boxFramesToReset;
    // The default width of the bounding box
    private int mBoxWidth;
    // The default height of the bounding box
    private int mBoxHeight;
    // The counter of the lost frames. Used in reseting the bounding box
    private int mFramesLost = 0;
    // Defines the last recorded position of the detected blob
    private PointF mLastBlob;

    // Defines the minimum size a blob has to be in order for it to be accepted as ball
    private final int minBlobSize;

    private final int hsvDivisor;
    private final float saturationMultiplier;
    private final float valueMultiplier;

    private final int mulDeltaX;
    private final int mulDeltaY;
    private final int mulDeltaWidth;
    private final int mulDeltaHeight;
    private final int minWidth;
    private final int minHeight;

    private final int minAddition;
    private final int maxAddition;

    private int threshold;

    private FeatureDetector mDetector;

    private int mMinAllowed;
    private int mMaxAllowed;

    public ColorDetector() {
        threshold = ConfigManager.getInt("recognition.def_thresh");
        mBoxWidth = ConfigManager.getInt("recognition.def_box_width");
        mBoxHeight = ConfigManager.getInt("recognition.def_box_height");
        boxFramesToReset = ConfigManager.getInt("recognition.reset_box_frames");
        minBlobSize = ConfigManager.getInt("recognition.min_blob_size");
        saturationMultiplier = ConfigManager.getFloat("multipliers.saturation");
        valueMultiplier = ConfigManager.getFloat("multipliers.value");
        mulDeltaX = ConfigManager.getInt("multipliers.delta_x");
        mulDeltaY = ConfigManager.getInt("multipliers.delta_x");
        mulDeltaWidth = ConfigManager.getInt("multipliers.delta_width");
        mulDeltaHeight = ConfigManager.getInt("multipliers.delta_height");
        minWidth = ConfigManager.getInt("recognition.min_box_width");
        minHeight = ConfigManager.getInt("recognition.min_box_height");
        minAddition = ConfigManager.getInt("recognition.min_addition");
        maxAddition = ConfigManager.getInt("recognition.min_addition");
        hsvDivisor = ConfigManager.getInt("trace.hsv_divisor");

        mBox = new RectF();
        mDetector = FeatureDetector.create(FeatureDetector.DYNAMIC_SIMPLEBLOB);
    }

    /**
     * Detect a ball from a given image
     * @param hsv the hsv values of the ball, which is to be detected
     * @param rect defines the rectangle of the blob
     * @param image defines the image, which is to be filtered
     * @return true if a ball is found
     */
    public boolean detectBallFromImage(Scalar hsv, Rect rect, Mat image) {
        // Convert RGB to HSV
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV);

        // Calculate the lower and upper bounds
        Scalar lowerLimit = getBoundingBox(hsv, -1);
        Scalar upperLimit = getBoundingBox(hsv, 1);

        // Filter the hsv image by color
        Core.inRange(image, lowerLimit, upperLimit, image);

        MatOfKeyPoint blobs = new MatOfKeyPoint();
        mDetector.detect(image, blobs);

        if ( mFramesLost > boxFramesToReset || !mBoxSet) {
            mBox = new RectF(((float)image.size().width - mBoxWidth) / 2,
                    ((float)image.size().height - mBoxHeight) / 2,
                    ((float)image.size().width + mBoxWidth) / 2,
                    ((float)image.size().height + mBoxHeight) / 2);
            mFramesLost = 0;
            mBoxSet = true;
        }

        if (blobs.size().empty()) {
            mFramesLost++;
            return false;
        }

        KeyPoint biggestBlob = getBiggestBlob(blobs.toArray());


        updateBoundingBox(biggestBlob);

        if (biggestBlob == null) {
            mFramesLost++;
            return false;
        }

        rect.left = (int)(biggestBlob.pt.x - (biggestBlob.size / 2));
        rect.top = (int)(biggestBlob.pt.y - (biggestBlob.size / 2));
        rect.right = (int)(biggestBlob.pt.x + (biggestBlob.size / 2));
        rect.bottom = (int)(biggestBlob.pt.y - (biggestBlob.size / 2));

        return true;
    }

    private KeyPoint getBiggestBlob(KeyPoint[] blobs) {
        KeyPoint biggestBlob = null;
        for (KeyPoint blob : blobs) {
            if (blob.size < mMinAllowed || blob.size > mMaxAllowed ||
                    !(mBox.contains((float)blob.pt.x, (float)blob.pt.y))) continue;

            if (mMinAllowed == 0) mMinAllowed = (int)blob.size;
                else if (mMinAllowed > blob.size) mMinAllowed = (int)blob.size - minAddition;

            if (mMaxAllowed < blob.size) mMaxAllowed = (int)blob.size + maxAddition;

            biggestBlob = blob;
            mFramesLost = 0;
            mLastBlob = new PointF((float)blob.pt.x, (float)blob.pt.y);
            break;
        }
        return biggestBlob;
    }

    private Scalar getBoundingBox(Scalar hsv, int boundIndicator) {
        double lowerHue = hsv.val[0] + (threshold / (double) hsvDivisor) * boundIndicator;
        double lowerSaturation = hsv.val[1] + (threshold * saturationMultiplier) * boundIndicator;
        double lowerValue = hsv.val[2] + (threshold * valueMultiplier) * boundIndicator;

        return new Scalar(lowerHue, lowerSaturation, lowerValue);
    }

    private void updateBoundingBox(KeyPoint blob) {
        if (blob == null) return;
        if (mLastBlob == null) return;

        float toAddX = mLastBlob.x - (float)blob.pt.x;
        float toAddY = mLastBlob.y - (float)blob.pt.y;

        if (toAddX < 0) toAddX *= -1;
        if (toAddY < 0) toAddY *= -1;

        float addX = toAddX * mulDeltaX;
        float addY = toAddY * mulDeltaY;

        if (blob.size > minBlobSize) {
            addX += blob.size * mulDeltaWidth;
            addY += blob.size * mulDeltaHeight;
        } else {
            addX += minWidth;
            addY += minHeight;
        }

        addX /= 2;
        addY /= 2;

        mBox = new RectF((float)blob.pt.x - addX,
                        (float)blob.pt.y - addY,
                        (float)blob.pt.x + addX,
                        (float)blob.pt.y + addY);
    }

    /**
     * Returns the current shared hue, saturation and value threshold,
     *  used in detecting the ball
     * @return The integer value of the threshold
     */
    public int getThreshold() {
        return threshold;
    }
}
