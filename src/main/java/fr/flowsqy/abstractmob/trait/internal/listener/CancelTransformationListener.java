package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class CancelTransformationListener extends TraitListener {

    public CancelTransformationListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        super(plugin, traitListenerManager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTransform(EntityTransformEvent event) {
        final Entity entity = event.getEntity();
        final List<MetadataValue> values = entity.getMetadata(plugin.getCustomKeys().CANCEL_TRANSFORMATION.getKey());
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
