package krzysztofmaziarz.thesetproject.model;

public enum Color {
    RED,
    GREEN,
    PURPLE;

    public static Color classify(double[] color) {
        double min = Math.min(color[0], Math.min(color[1], color[2]));

        if (color[0] + 3 * min > 2 * (color[1] + color[2]) + 10) {
            return RED;
        } else if (color[1] > min + 14) {
            return GREEN;
        } else {
            return PURPLE;
        }
    }
}
