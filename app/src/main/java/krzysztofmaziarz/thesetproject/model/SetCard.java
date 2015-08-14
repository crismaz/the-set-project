package krzysztofmaziarz.thesetproject.model;

import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import krzysztofmaziarz.thesetproject.core.SetFigureUtils;

public class SetCard {
    private Color color;
    private Shading shading;
    private Shape shape;

    private Rect box;
    private List<SetFigure> figures;

    public static SetCard merge(SetCard first, SetCard second) {
        List<SetFigure> figures = new ArrayList<>();

        figures.addAll(first.getFigures());
        figures.addAll(second.getFigures());

        return new SetCard(figures);
    }

    public SetCard(SetFigure... figures) {
        this(Arrays.asList(figures));
    }

    public SetCard(List<SetFigure> figures) {
        this.figures = figures;

        color = figures.get(0).getColor();
        shading = figures.get(0).getShading();
        shape = figures.get(0).getShape();
        box = SetFigureUtils.getBoundingBox(figures);
    }

    public List<SetFigure> getFigures() {
        return figures;
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
}
