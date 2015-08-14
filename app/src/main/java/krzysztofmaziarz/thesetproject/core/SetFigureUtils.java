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

    public static boolean checkRatio(int height, int width) {
        return  height >= width * MIN_RATIO &&
                height <= width * MAX_RATIO;
    }

    public static boolean checkArea(int area, int imageArea) {
        return area >= imageArea * MIN_RELATIVE_AREA;
    }

    public static List<SetFigure> filterOutInnerFigures(List<SetFigure> list) {
        List<SetFigure> result = new ArrayList<>();

        for (SetFigure figure : list) {
            boolean inner = false;
            Rect box = figure.getBox();

            for (SetFigure outerFigure : list) {
                inner |= outerFigure.getBox().contains(box.tl()) &&
                         outerFigure.getBox().contains(box.br());
            }

            if (!inner) {
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

        return false; // TODO
    }

    public static boolean areSame(SetFigure first, SetFigure second) {
        return  first.getColor() == second.getColor() &&
                first.getShading() == second.getShading() &&
                first.getShape() == second.getShape();
    }

    public static boolean areJoinable(SetFigure first, SetFigure second) {
        return areClose(first, second) && areSame(first, second);
    }

    public static Rect getBoundingBox(List<SetFigure> figures) {
        return figures.get(0).getBox(); // TODO
    }

    public static List<SetCard> extractCards(List<SetFigure> figures) {
        List<SetCard> cards = new ArrayList<>();

        for (SetFigure figure : figures) {
            cards.add(new SetCard(figure));
        }

        for (int i = 0; i < figures.size(); i++) {
            for (int j = 0; j < figures.size(); j++) {
                if (areJoinable(figures.get(i), figures.get(j))) {
                    SetCard merged = SetCard.merge(cards.get(i), cards.get(j));

                    cards.set(i, merged);
                    cards.set(j, merged);
                }
            }
        }

        return new ArrayList<>(new HashSet<>(cards));
    }
}
