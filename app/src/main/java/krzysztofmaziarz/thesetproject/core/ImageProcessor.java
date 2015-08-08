package krzysztofmaziarz.thesetproject.core;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
    private static final int DEFAULT_MAT_TYPE = CvType.CV_8U;
    private static final double CANNY_LOWER_THRESHOLD = 50.0;
    private static final double CANNY_UPPER_THRESHOLD = 120.0;

    public static Bitmap markSets(Bitmap source) {
        Mat imageMat = new Mat(source.getHeight(), source.getWidth(), DEFAULT_MAT_TYPE);
        Utils.bitmapToMat(source, imageMat);

        Mat edges = new Mat(imageMat.rows(), imageMat.cols(), DEFAULT_MAT_TYPE);
        Imgproc.Canny(imageMat, edges, CANNY_LOWER_THRESHOLD, CANNY_UPPER_THRESHOLD);

        Utils.matToBitmap(edges, source);
        return source;
    }

}
