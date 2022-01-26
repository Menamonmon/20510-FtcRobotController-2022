package org.firstinspires.ftc.teamcode.utils;

import android.graphics.PostProcessor;

import com.google.ftcresearch.tfod.tracking.ObjectTracker;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


public class FrenzyOpenCVDetector extends OpenCvPipeline {
    private class Position {
        public final static String LEFT = "left";
        public final static String MIDDLE = "MIDDLE";
        public final static String RIGHT = "RIGHT";
    }
    String position;
    Telemetry telemetry;
    int width, height;
    Mat mat = new Mat();
    MatOfKeyPoint blobOutputKeypoints = new MatOfKeyPoint();

    public FrenzyOpenCVDetector(Telemetry t, int w, int h) {
        width = w;
        height = h;
        telemetry = t;
    }

    private void displayBlobKeyPoints() {
        for (KeyPoint kp : blobOutputKeypoints.toArray()) {
            telemetry.addData("BLOB DETECTION OUTPUT: ", String.format("(x: %f.2, y: %f.2", kp.pt.x, kp.pt.y));
        }
        telemetry.update();
    }

    private void updatePosition() {
        KeyPoint[] arr = blobOutputKeypoints.toArray();
        if (arr.length != 3) {
            telemetry.addData("ERROR WITH DETECTION: ", "NUMBER OF BLOBS DETECTED ARE NOT 3");
            displayBlobKeyPoints();
            return;
        }
        KeyPoint highestKP = null;
        KeyPoint leftMost = null;
        KeyPoint rightMost = null;
        double y = Double.POSITIVE_INFINITY;
        double lowestX = Double.POSITIVE_INFINITY;
        double highestX = Double.NEGATIVE_INFINITY;
        for (KeyPoint kp : arr) {
            if (kp.pt.y < y) {
                y = kp.pt.y;
                highestKP = kp;
            }
            if (kp.pt.x < lowestX) {
                lowestX = kp.pt.x;
                leftMost = kp;
            }
            if (kp.pt.x > highestX) {
                highestX = kp.pt.x;
                rightMost = kp;
            }
        }

        if (leftMost != null && highestKP != null && rightMost != null) {
            if (areKpEqual(leftMost, highestKP)) {
                position = Position.LEFT;
            } else if (areKpEqual(rightMost, highestKP)) {
                position = Position.RIGHT;
            } else {
                position = Position.MIDDLE;
            }
        }
        telemetry.addData("ELEMENT POSITON: ", position);
    }

    private boolean areKpEqual(KeyPoint kp1, KeyPoint kp2) {
        return kp1.pt.x == kp2.pt.x && kp1.pt.y == kp2.pt.y;
    }

    private byte saturate(double val) {
        int iVal = (int) Math.round(val);
        iVal = iVal > 255 ? 255 : (iVal < 0 ? 0 : iVal);
        return (byte) iVal;
    }

    private Mat contrastFilterImage(Mat image, int beta) {
        Mat newImage = Mat.zeros(image.size(), image.type());
        double alpha;
        if (beta == 255) {
            alpha = Double.POSITIVE_INFINITY;

        } else {
            alpha = (255 + beta) / (double) (255 - beta);
        }
        byte[] imageData = new byte[(int) (image.total()*image.channels())];
        image.get(0, 0, imageData);
        byte[] newImageData = new byte[(int) (newImage.total()*newImage.channels())];
        for (int y = 0; y < image.rows(); y++) {
            for (int x = 0; x < image.cols(); x++) {
                for (int c = 0; c < image.channels(); c++) {
                    double pixelValue = imageData[(y * image.cols() + x) * image.channels() + c];
                    pixelValue = pixelValue < 0 ? pixelValue + 256 : pixelValue;
                    newImageData[(y * image.cols() + x) * image.channels() + c]
                            = saturate(alpha * pixelValue + beta);
                }
            }
        }
        newImage.put(0, 0, newImageData);
        return newImage;
    }

    public Mat processFrame(Mat input) {
        // Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        // Scalar lowerHSV = new Scalar(15, 150, 20);
        // Scalar upperHSV = new Scalar(35, 255, 255);
        // Core.inRange(mat, lowerHSV, upperHSV, mat);
        // Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);
        Imgproc.resize(input, mat, new Size(width, height));
        int cvCvtcolor0Code = Imgproc.COLOR_RGB2BGR;
        Imgproc.cvtColor(mat, mat, cvCvtcolor0Code);

        // Step CV_cvtColor1:
        int cvCvtcolor1Code = Imgproc.COLOR_BGR2RGB;
        Imgproc.cvtColor(mat, mat, cvCvtcolor1Code);

        // Step CV_Threshold0:
        double cvThresholdThresh = 220.0;
        double cvThresholdMaxval = 10000.0;
        int cvThresholdType = Imgproc.THRESH_BINARY;
        Imgproc.threshold(mat, mat, cvThresholdThresh, cvThresholdMaxval, cvThresholdType);

        // Step Find_Blobs0:
        // Mat findBlobsInput = cvThresholdOutput;
        double findBlobsMinArea = 190.0;
        double[] findBlobsCircularity = {0.34172661870503596, 1.0};
        findBlobs(mat, findBlobsMinArea, findBlobsCircularity, false, blobOutputKeypoints);
        updatePosition();
        displayBlobKeyPoints();
        Features2d.drawKeypoints(mat, blobOutputKeypoints, mat);
        return mat;
    }

    private void findBlobs(Mat input, double minArea, double[] circularity,
                           Boolean darkBlobs, MatOfKeyPoint blobList) {
        SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();

        // Setting the parameters for the blob detector
        params.set_thresholdStep(10);
        params.set_minThreshold(50);
        params.set_maxThreshold(220);
        params.set_minRepeatability(2);
        params.set_minDistBetweenBlobs(10);
        params.set_filterByColor(false);
        params.set_filterByArea(true);
        params.set_minArea((float) minArea);
        params.set_maxArea((float) (Integer.MAX_VALUE));
        params.set_filterByCircularity(true);
        params.set_minCircularity((float) circularity[0]);
        params.set_maxCircularity((float) circularity[1]);
        params.set_filterByInertia(true);
        params.set_minInertiaRatio((float) 0.1);
        params.set_maxInertiaRatio(Integer.MAX_VALUE);
        params.set_filterByConvexity(true);
        params.set_minConvexity((float) 0.95);
        params.set_maxConvexity((float) Integer.MAX_VALUE);

        SimpleBlobDetector blobDet = SimpleBlobDetector.create(params);
        blobDet.detect(input, blobList);
    }

    public String getPosition() {
        return position;
    }
}
