package fr.flowsqy.abstractmob.key;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.updater.KeyUpdaters;

import java.util.LinkedList;
import java.util.List;

public class CustomKeys {

    public final CustomKey LIGHTNING_ON_DEATH;
    public final CustomKey KNOCKBACK_UP;
    public final CustomKey WEB_ON_WALK;
    public final CustomKey PROJECTILE_RESISTANCE;
    public final CustomKey SUN_RESISTANCE;

    private final List<CustomKey> keys;

    public CustomKeys(AbstractMobPlugin plugin) {
        keys = new LinkedList<>();
        final KeyUpdaters keyUpdaters = plugin.getKeyUpdaters();
        LIGHTNING_ON_DEATH = register(new CustomKey("lightning_on_death", keyUpdaters.CHANCES, plugin));
        KNOCKBACK_UP = register(new CustomKey("knockback_up", keyUpdaters.DOUBLE, plugin));
        WEB_ON_WALK = register(new CustomKey("web_on_walk", keyUpdaters.CHANCES, plugin));
        PROJECTILE_RESISTANCE = register(new CustomKey("projectile_resistance", keyUpdaters.BOOLEAN, plugin));
        SUN_RESISTANCE = register(new CustomKey("sun_resistance", keyUpdaters.BOOLEAN, plugin));
    }

    private CustomKey register(CustomKey customKey) {
        keys.add(customKey);
        return customKey;
    }

    public List<CustomKey> getKeys() {
        return keys;
    }

}
