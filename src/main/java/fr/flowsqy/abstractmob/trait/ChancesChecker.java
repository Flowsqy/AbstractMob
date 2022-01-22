package fr.flowsqy.abstractmob.trait;

import java.util.Random;

public class ChancesChecker {

    private final static int MAX_CHANCES = 100_000;

    private final Random random;

    public ChancesChecker() {
        this.random = new Random();
    }


    public boolean canPerform(int chances) {
        return chances == 0 || random.nextInt(MAX_CHANCES) < chances;
    }
}
