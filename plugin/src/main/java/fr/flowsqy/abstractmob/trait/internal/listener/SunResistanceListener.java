package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class SunResistanceListener extends TraitListener {

    public SunResistanceListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        super(plugin, traitListenerManager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBurn(EntityCombustEvent event) {
        // Check if it's not sun
        final Class<?> eventClass = event.getClass();
        if (eventClass == EntityCombustByBlockEvent.class || eventClass == EntityCombustByEntityEvent.class) {
            return;
        }

        final Entity entity = event.getEntity();
        final List<MetadataValue> values = entity.getMetadata(plugin.getCustomKeys().SUN_RESISTANCE.getKey());
        if (values.isEmpty()) {
            return;
        }
        if (values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .anyMatch(MetadataValue::asBoolean)
        ) {
            event.setCancelled(true);
        }
    }

}
