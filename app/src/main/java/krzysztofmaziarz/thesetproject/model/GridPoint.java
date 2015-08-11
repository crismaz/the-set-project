package krzysztofmaziarz.thesetproject.model;

public class GridPoint {
    public int x, y;

    /**
     * @param coordinates coordinate array given by matOfPoint.get()
     */
    public GridPoint(double[] coordinates) {
        x = (int) Math.round(coordinates[1]);
        y = (int) Math.round(coordinates[0]);
    }
}
