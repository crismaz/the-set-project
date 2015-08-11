package krzysztofmaziarz.thesetproject.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class SetFigure {
    private static final double MIN_RATIO = 1.5;
    private static final double MAX_RATIO = 3.0;

    private Mat image;
    private MatOfPoint contour;
    private Rect box;

    private boolean valid;

    private Color color;
    private Shading shading;
    private Shape shape;

    public SetFigure(Mat image, MatOfPoint contour) {
        this.image = image;
        this.contour = contour;

        initialize();
    }

    private void initialize() {
        box = Imgproc.boundingRect(contour);
        computeValid();

        if (valid) {
            computeColor();
            computeShading();
            computeShape();
        }
    }

    private void computeValid() {
        valid = checkRatio(box.height, box.width);
    }

    private void computeColor() {
        // TODO
    }

    private void computeShading() {
        // TODO
    }

    private void computeShape() {
        // TODO
    }

    public boolean isValid() {
        return valid;
    }

    public Rect getBox() {
        return box;
    }

    private static boolean checkRatio(int height, int width) {
        return  height >= width * MIN_RATIO &&
                height <= width * MAX_RATIO;
    }
}
