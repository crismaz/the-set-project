package krzysztofmaziarz.thesetproject.core;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import krzysztofmaziarz.thesetproject.model.SetFigure;

public class ImageProcessor {
    private static final int DEFAULT_MAT_TYPE = CvType.CV_8U;
    private static final double CANNY_LOWER_THRESHOLD = 100.0;
    private static final double CANNY_UPPER_THRESHOLD = 250.0;

    public static Bitmap markSets(Bitmap source) {
        int imageHeight = source.getHeight();
        Mat imageMat = new Mat(source.getHeight(), source.getWidth(), DEFAULT_MAT_TYPE);
        Utils.bitmapToMat(source, imageMat);

        Mat edges = new Mat(imageMat.rows(), imageMat.cols(), DEFAULT_MAT_TYPE);
        Imgproc.Canny(imageMat, edges,
                CANNY_LOWER_THRESHOLD,
                CANNY_UPPER_THRESHOLD);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        for (MatOfPoint contour : contours) {
            if (normalize(contour.rows(), imageHeight) > 0.05) {
                Rect box = Imgproc.boundingRect(contour);

                if (SetFigure.checkRatio(box.height, box.width)) {
                    Core.rectangle(imageMat, box.tl(), box.br(), new Scalar(1));
                }
            }
        }

        Utils.matToBitmap(imageMat, source);
        return source;
    }

    private static double normalize(int value, int imageHeight) {
        return (double) value / imageHeight;
    }
}
