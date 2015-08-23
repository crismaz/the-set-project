package krzysztofmaziarz.thesetproject.model;

import org.opencv.core.Point;

public class GridPoint {
    public int row, col;

    /**
     * @param coordinates coordinate array given by matOfPoint.get()
     */
    public GridPoint(double[] coordinates) {
        col = (int) Math.round(coordinates[0]);
        row = (int) Math.round(coordinates[1]);
    }

    public Point asPoint() {
        return new Point(col, row);
    }
}
