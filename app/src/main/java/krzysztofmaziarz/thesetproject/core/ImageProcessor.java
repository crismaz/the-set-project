package krzysztofmaziarz.thesetproject.core;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import krzysztofmaziarz.thesetproject.model.Color;
import krzysztofmaziarz.thesetproject.model.SetCard;
import krzysztofmaziarz.thesetproject.model.SetFigure;

public class ImageProcessor {
    private static final int DEFAULT_MAT_TYPE = CvType.CV_8U;
    private static final double CANNY_LOWER_THRESHOLD = 100.0;
    private static final double CANNY_UPPER_THRESHOLD = 250.0;
    private static final double MIN_CONTOUR_SIZE = 0.05;

    public static Bitmap markSets(Bitmap source) {
        Mat imageMat = new Mat(source.getHeight(), source.getWidth(), DEFAULT_MAT_TYPE);
        Utils.bitmapToMat(source, imageMat);

        Mat edges = getEdges(imageMat);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        List<SetFigure> figures = getFigures(imageMat, edges);
        List<SetCard> cards = SetUtils.extractCards(figures);

        markCards(imageMat, cards);

        Collection<Collection<SetCard>> sets = SetUtils.getSets(cards);

        char letter = 'A';
        for (Collection<SetCard> set : sets) {
            for (SetCard card : set) {
                Point point = SetUtils.getCenter(card.getBox()).asPoint();

                point.x += 30 * (letter - 'A') - 45;
                point.y += 15;

                Core.putText(imageMat, String.valueOf(letter), point,
                        Core.FONT_HERSHEY_PLAIN, 3.0, new Scalar(1), 2);
            }
            letter++;
        }

        Utils.matToBitmap(imageMat, source);
        return source;
    }

    private static void markCards(Mat imageMat, List<SetCard> cards) {
        for (SetCard card : cards) {
            Scalar scalar;

            if (card.getColor() == Color.RED) {
                scalar = new Scalar(255, 0, 0, 0);
            } else if (card.getColor() == Color.GREEN) {
                scalar = new Scalar(0, 255, 0, 0);
            } else {
                scalar = new Scalar(0, 0, 255, 0);
            }

            Rect box = card.getBox();

            Core.rectangle(imageMat, box.tl(), box.br(), scalar);
            Core.putText(imageMat, card.getShading().toString(), box.tl(),
                    Core.FONT_HERSHEY_PLAIN, 1.0, scalar);
            Core.putText(imageMat, card.getShape().toString(), new Point(box.x, box.y + box.height),
                    Core.FONT_HERSHEY_PLAIN, 1.0, scalar);
        }
    }

    private static List<SetFigure> getFigures(Mat imageMat, Mat edges) {
        int imageHeight = imageMat.rows();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        List<SetFigure> figures = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            if (normalize(contour.rows(), imageHeight) > MIN_CONTOUR_SIZE) {
                SetFigure figure = new SetFigure(imageMat, contour);

                if (figure.isValid()) {
                    figures.add(figure);
                }
            }
        }

        return SetUtils.filterOutInnerFigures(figures);
    }

    private static Mat getEdges(Mat imageMat) {
        Mat edges = new Mat(imageMat.rows(), imageMat.cols(), DEFAULT_MAT_TYPE);
        Imgproc.Canny(imageMat, edges,
                CANNY_LOWER_THRESHOLD,
                CANNY_UPPER_THRESHOLD);

        return edges;
    }

    private static double normalize(int value, int imageHeight) {
        return (double) value / imageHeight;
    }
}
