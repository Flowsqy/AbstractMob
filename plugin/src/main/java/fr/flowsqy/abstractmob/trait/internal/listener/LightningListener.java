package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;

public class LightningListener extends TraitListener {

    public LightningListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        super(plugin, traitListenerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final List<MetadataValue> values = entity.getMetadata(plugin.getCustomKeys().LIGHTNING_ON_DEATH.getKey());
        if (values.isEmpty()) {
            return;
        }
        final Optional<MetadataValue> pluginValue = values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .findAny();
        if (pluginValue.isEmpty()) {
            return;
        }
        // Check chances and fire the lightning
        if (plugin.getChancesChecker().canPerform(pluginValue.get().asInt())) {
            entity.getWorld().strikeLightning(entity.getLocation());
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
    }

}
