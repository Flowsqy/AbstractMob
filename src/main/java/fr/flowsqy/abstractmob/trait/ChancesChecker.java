package fr.flowsqy.abstractmob.trait;

import java.util.Random;

public class ChancesChecker {

    private final static int FULL_CHANCES = 100;
    private final static int ACCURACY_FACTOR = 1000;
    private final static int MAX_CHANCES = FULL_CHANCES * ACCURACY_FACTOR;

    private final Random random;

    public ChancesChecker() {
        this.random = new Random();
    }

    /**
     * Get the value used by the plugin to perform chance calculations from a percentage
     *
     * @param value The classic percentage value
     * @return The plugin value of the given percentage
     */
    public static int classicToPlugin(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("The chances can not be below 0");
        }
        if (value > FULL_CHANCES) {
            throw new IllegalArgumentException("The chances can not be > 100");
        }
        return value == FULL_CHANCES ? 0 : (value * ACCURACY_FACTOR);
    }

    /**
     * Get the percentage value from the value used by the plugin to perform chance calculations
     *
     * @param value The plugin value
     * @return The classic percentage value
     */
    public static int pluginToClassic(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("The chances can not be below 0");
        }
        if (value >= MAX_CHANCES) {
            throw new IllegalArgumentException("The chances can not be > 100");
        }
        return value == 0 ? FULL_CHANCES : (value / ACCURACY_FACTOR);
    }

    public boolean canPerform(int chances) {
        return chances == 0 || random.nextInt(MAX_CHANCES) < chances;
    }

}
