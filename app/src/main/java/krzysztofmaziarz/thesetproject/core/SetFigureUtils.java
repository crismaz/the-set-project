package krzysztofmaziarz.thesetproject.core;

import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import krzysztofmaziarz.thesetproject.model.GridPoint;
import krzysztofmaziarz.thesetproject.model.SetCard;
import krzysztofmaziarz.thesetproject.model.SetFigure;

public class SetFigureUtils {
    private static final double MIN_RATIO = 1.5;
    private static final double MAX_RATIO = 3.0;
    private static final double MIN_RELATIVE_AREA = 0.0005;
    private static final double MAX_JOIN_RELATIVE_DISTANCE = 1.8;
    private static final double MIN_COMMON_RELATIVE_AREA = 0.9;

    public static boolean checkRatio(int height, int width) {
        return  height >= width * MIN_RATIO &&
                height <= width * MAX_RATIO;
    }

    public static boolean checkArea(int area, int imageArea) {
        return area >= imageArea * MIN_RELATIVE_AREA;
    }

    public static boolean isInnerFigure(SetFigure inner, SetFigure outer) {
        if (inner.getBox().area() >= outer.getBox().area()) {
            return false;
        }

        double commonArea = getCommonArea(inner.getBox(), outer.getBox());
        return commonArea > MIN_COMMON_RELATIVE_AREA * inner.getBox().area();
    }

    public static double getCommonArea(Rect first, Rect second) {
        return getCommonBox(first, second).area();
    }

    public static List<SetFigure> filterOutInnerFigures(List<SetFigure> list) {
        List<SetFigure> result = new ArrayList<>();

        for (SetFigure figure : list) {
            boolean isInner = false;

            for (SetFigure outerFigure : list) if (figure != outerFigure) {
                isInner |= isInnerFigure(figure, outerFigure);
            }

            for (SetFigure addedFigure : result) {
                if (addedFigure.getBox().equals(figure.getBox())) {
                    isInner = true;
                }
            }

            if (!isInner) {
                result.add(figure);
            }
        }

        return result;
    }

    public static boolean areClose(SetFigure first, SetFigure second) {
        GridPoint centerFirst = first.getCenter();
        GridPoint centerSecond = second.getCenter();

        double distance = Math.sqrt(
                Math.pow(centerFirst.x - centerSecond.x, 2) +
                Math.pow(centerFirst.y - centerSecond.y, 2)
        );

        distance /= first.getBox().width;
        return distance < MAX_JOIN_RELATIVE_DISTANCE;
    }

    public static boolean areSimilar(SetFigure first, SetFigure second) {
        return  first.getColor() == second.getColor() &&
                first.getShading() == second.getShading() &&
                first.getShape() == second.getShape();
    }

    public static boolean areJoinable(SetFigure first, SetFigure second) {
        return areClose(first, second) && areSimilar(first, second);
    }

    public static Rect getBoundingBox(Rect first, Rect second) {
        int xMin = Math.min(first.x, second.x);
        int xMax = Math.max(first.x + first.width, second.x + second.width);
        int yMin = Math.min(first.y, second.y);
        int yMax = Math.max(first.y + first.height, second.y + second.height);

        return new Rect(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    public static Rect getCommonBox(Rect first, Rect second) {
        int xMin = Math.max(first.x, second.x);
        int xMax = Math.min(first.x + first.width, second.x + second.width);
        int yMin = Math.max(first.y, second.y);
        int yMax = Math.min(first.y + first.height, second.y + second.height);

        return new Rect(xMin, yMin, Math.max(0, xMax - xMin), Math.max(0, yMax - yMin));
    }

    public static Rect getBoundingBox(List<SetFigure> figures) {
        Rect box = figures.get(0).getBox();

        for (SetFigure figure : figures) {
            box = getBoundingBox(box, figure.getBox());
        }

        return box;
    }

    public static List<SetCard> extractCards(List<SetFigure> figures) {
        List<SetCard> cards = new ArrayList<>();

        for (SetFigure figure : figures) {
            cards.add(new SetCard(figure));
        }

        for (int i = 0; i < figures.size(); i++) {
            for (int j = i + 1; j < figures.size(); j++) {
                if (areJoinable(figures.get(i), figures.get(j))) {
                    SetCard merged = SetCard.merge(cards.get(i), cards.get(j));

                    for (int k = 0; k < cards.size(); k++) {
                        if (cards.get(k) == cards.get(i) || cards.get(k) == cards.get(j)) {
                            cards.set(k, merged);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(new HashSet<>(cards));
    }
}
