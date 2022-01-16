package fr.flowsqy.abstractmob.key;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.updater.KeyUpdater;
import org.bukkit.NamespacedKey;

public enum Keys {

    LIGHTNING_ON_DEATH("lightning_on_death", KeyUpdater.Commons.CHANCES),
    KNOCKBACK_UP("knockback_up", KeyUpdater.Commons.DOUBLE),
    WEB_ON_WALK("web_on_walk", KeyUpdater.Commons.CHANCES);

    private final String key;
    private final KeyUpdater updater;
    private NamespacedKey namespacedKey;

    Keys(String key, KeyUpdater updater) {
        this.key = key;
        this.updater = updater;
    }

    public static void initKeys(AbstractMobPlugin plugin) {
        for (Keys key : Keys.values()) {
            if (key.namespacedKey != null) {
                throw new IllegalStateException("The key is already initialized");
            }
            key.namespacedKey = new NamespacedKey(plugin, key.key);
        }
    }

    public String getKey() {
        return key;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public KeyUpdater getUpdater() {
        return updater;
    }

}
