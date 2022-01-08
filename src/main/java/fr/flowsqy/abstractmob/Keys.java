package fr.flowsqy.abstractmob;

import org.bukkit.NamespacedKey;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class Keys {

    public final static String LIGHTING_ON_DEATH = "lightning_on_death";
    public final static String KNOCKBACK = "knockback";
    public static NamespacedKey LIGHTING_ON_DEATH_KEY;
    public static MetadataValue TRUE_VALUE;

    public static void init(Plugin plugin){
        if(LIGHTING_ON_DEATH_KEY != null){
            throw new IllegalStateException("Keys are already initialized");
        }
        LIGHTING_ON_DEATH_KEY = new NamespacedKey(plugin, LIGHTING_ON_DEATH);
        TRUE_VALUE = new FixedMetadataValue(plugin, true);
    }

}
