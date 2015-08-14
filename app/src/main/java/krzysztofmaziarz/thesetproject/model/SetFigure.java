package krzysztofmaziarz.thesetproject.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import krzysztofmaziarz.thesetproject.core.SetFigureUtils;

public class SetFigure {
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
        valid = SetFigureUtils.checkRatio(box.height, box.width) &&
                SetFigureUtils.checkArea(box.height * box.width, image.height() * image.width());
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
        Map<Integer,Integer> left = new HashMap<>();
        Map<Integer,Integer> right = new HashMap<>();

        for (GridPoint point : points) {
            if (left.get(point.x) == null || left.get(point.x) > point.y) {
                left.put(point.x, point.y);
            }

            if (right.get(point.x) == null || right.get(point.x) < point.y) {
                right.put(point.x, point.y);
            }
        }

        Collection<Integer> differences = new ArrayList<>();

        for (Integer x : left.keySet()) {
            Integer leftBound = left.get(x);
            Integer rightBound = right.get(x);

            if (leftBound < rightBound) {
                differences.add(rightBound - leftBound);
            }
        }

        double mean = 0.0;

        for (Integer diff : differences) {
            mean += diff;
        }

        mean /= differences.size();

        double variance = 0.0;

        for (Integer diff : differences) {
            variance += Math.pow(diff - mean, 2);
        }

        variance /= differences.size();

        shape = Shape.classify(mean / box.width, Math.sqrt(variance) / box.width);
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
}
