package krzysztofmaziarz.thesetproject.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

public class SetFigure {
    private static final double MIN_RATIO = 1.5;
    private static final double MAX_RATIO = 3.0;

    Mat image;
    MatOfPoint contour;

    boolean valid;

    Color color;
    Shading shading;
    Shape shape;

    public SetFigure(Mat image, MatOfPoint contour) {
        this.image = image;
        this.contour = contour;
    }

    public static boolean checkRatio(int height, int width) {
        return  height >= width * MIN_RATIO &&
                height <= width * MAX_RATIO;
    }
}
