package krzysztofmaziarz.thesetproject.model;

public enum Shading {
    SOLID,
    STRIPED,
    OPEN;

    public static Shading classify(double brightnessChange) {
        if (brightnessChange > 130.0) {
            return OPEN;
        } else if (brightnessChange > 0.0) {
            return STRIPED;
        } else {
            return SOLID;
        }
    }
}
