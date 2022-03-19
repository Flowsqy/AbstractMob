package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.CustomKey;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class TransferTraitsListener extends TraitListener {

    public TransferTraitsListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        super(plugin, traitListenerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTransform(EntityTransformEvent event) {
        final Entity entity = event.getEntity();
        final List<MetadataValue> values = entity.getMetadata(plugin.getCustomKeys().TRANSFER_TRAITS.getKey());
        if (values.isEmpty()) {
            return;
        }
        if (values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .anyMatch(MetadataValue::asBoolean)
        ) {
            // Transfer every trait to the future entity
            for (CustomKey customKey : plugin.getCustomKeys().getKeys()) {
                final List<MetadataValue> keyValues = entity.getMetadata(customKey.getKey());
                if (keyValues.isEmpty()) {
                    continue;
                }
                // For every custom key value stored in the original entity
                keyValues.stream()
                        .filter(value -> value.getOwningPlugin() == plugin)
                        .forEach(value -> {
                            // Transfer the custom key value to the transformed entities
                            for (Entity transformedEntity : event.getTransformedEntities()) {
                                transformedEntity.setMetadata(customKey.getKey(), value);
                            }
                        });
            }

            // Unload the original entity
            plugin.getTraitLauncherTask().unloadEntities(event.getEntity());

            final Entity[] newEntities = event.getTransformedEntities().toArray(new Entity[0]);
            // Load entity tasks
            plugin.getTraitTaskManager().loadEntities(newEntities);
            // Save every entity traits in the permanent storage
            plugin.getUpdateTask().saveEntities(newEntities);
        }
    }

}
