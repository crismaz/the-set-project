package krzysztofmaziarz.thesetproject.core;

import android.hardware.Camera;

import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

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
}
