package krzysztofmaziarz.thesetproject.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class SetFigure {
    private static final double MIN_RATIO = 1.5;
    private static final double MAX_RATIO = 3.0;
    private static final double MIN_RELATIVE_AREA = 0.0005;

    private Mat image;
    private MatOfPoint contour;
    private List<GridPoint> points;
    private Rect box;

    private boolean valid;

    private Color color;
    private Shading shading;
    private Shape shape;

    private double[] avgPixelColor;

    public SetFigure(Mat image, MatOfPoint contour) {
        this.image = image;
        this.contour = contour;

        points = new ArrayList<>();

        for (int i = 0; i < contour.rows(); i++)
            points.add(new GridPoint(contour.get(i, 0)));

        for (GridPoint point : points) {
            point.x = Math.max(point.x, 0);
            point.y = Math.max(point.y, 0);
            point.x = Math.min(point.x, image.rows() - 1);
            point.y = Math.min(point.y, image.cols() - 1);
        }

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
        valid = checkRatio(box.height, box.width) &&
                checkArea(box.height * box.width, image.height() * image.width());
    }

    private void computeColor() {
        avgPixelColor = new double[3];

        for (GridPoint point : points) {
            double[] color = image.get(point.x, point.y);

            for (int i = 0; i < 3; i++) {
                avgPixelColor[i] += color[i];
            }
        }

        for (int i = 0; i < 3; i++) {
            avgPixelColor[i] /= points.size();
        }

        color = Color.classify(avgPixelColor);
    }

    private void computeShading() {
        double[] centerPixelColor = image.get(getCenter().x, getCenter().y);

        double change = 0.0;
        for (int i = 0; i < 3; i++) {
            change += centerPixelColor[i] - avgPixelColor[i];
        }

        shading = Shading.classify(change);
    }

    private void computeShape() {
        // TODO
    }

    public boolean isValid() {
        return valid;
    }

    public Color getColor() {
        return color;
    }

    public Shape getShape() {
        return shape;
    }

    public Shading getShading() {
        return shading;
    }

    public Rect getBox() {
        return box;
    }

    public GridPoint getCenter() {
        return new GridPoint(new double[] {
                (box.br().x + box.tl().x) / 2,
                (box.br().y + box.tl().y) / 2
        });
    }

    private static boolean checkRatio(int height, int width) {
        return  height >= width * MIN_RATIO &&
                height <= width * MAX_RATIO;
    }

    private static boolean checkArea(int area, int imageArea) {
        return area >= imageArea * MIN_RELATIVE_AREA;
    }
}
