package fr.flowsqy.abstractmob.updater;

import fr.flowsqy.abstractmob.AbstractMobPlugin;

public class KeyUpdaters {

    public final KeyUpdater.AbstractUpdater BOOLEAN;
    public final KeyUpdater.AbstractUpdater INTEGER;
    public final KeyUpdater.AbstractUpdater DOUBLE;
    public final KeyUpdater.AbstractUpdater CHANCES;
    public final KeyUpdater.StringUpdater STRING;

    public KeyUpdaters(AbstractMobPlugin plugin) {
        BOOLEAN = new KeyUpdater.BooleanUpdater(plugin);
        INTEGER = new KeyUpdater.IntegerUpdater(plugin);
        DOUBLE = new KeyUpdater.DoubleUpdater(plugin);
        CHANCES = new KeyUpdater.ChancesUpdater(plugin);
        STRING = new KeyUpdater.StringUpdater(plugin);
    }

}
