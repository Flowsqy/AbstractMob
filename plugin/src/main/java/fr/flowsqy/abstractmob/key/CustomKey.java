package fr.flowsqy.abstractmob.key;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.updater.KeyUpdater;
import org.bukkit.NamespacedKey;

public class CustomKey {

    private final String key;
    private final KeyUpdater updater;
    private final NamespacedKey namespacedKey;

    public CustomKey(String key, KeyUpdater updater, AbstractMobPlugin plugin) {
        this.key = key;
        this.updater = updater;
        namespacedKey = new NamespacedKey(plugin, key);
    }

    public String getKey() {
        return key;
    }

    public KeyUpdater getUpdater() {
        return updater;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

}
