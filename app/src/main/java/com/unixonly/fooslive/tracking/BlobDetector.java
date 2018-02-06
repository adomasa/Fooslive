package com.unixonly.fooslive.tracking;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;

/**
 * Created by paulius on 1/27/18.
 */

public class BlobDetector {
    private FeatureDetector mDetector;
    public BlobDetector() {
        mDetector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
    }
    public void getBlobs(Mat filteredImage, MatOfKeyPoint blobs) {
        mDetector.detect(filteredImage, blobs);
    }
}
