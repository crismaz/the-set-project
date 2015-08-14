package krzysztofmaziarz.thesetproject.model;

public enum Shape {
    DIAMOND,
    SQUIGGLE,
    OVAL;

    public static Shape classify(double mean, double deviation) {
        if (mean > 0.8) {
            return OVAL;
        } else if (deviation > 0.2) {
            return DIAMOND;
        } else {
            return SQUIGGLE;
        }
    }
}
