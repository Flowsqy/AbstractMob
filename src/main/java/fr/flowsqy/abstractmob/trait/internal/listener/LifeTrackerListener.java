package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.metadata.MetadataValue;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class LifeTrackerListener extends TraitListener {

    private final DecimalFormat maxLifeFormat = new DecimalFormat("####0");
    private final DecimalFormat lifeFormat = new DecimalFormat("####0.#");
    private final DecimalFormat percentageFormat = new DecimalFormat("##0");

    public LifeTrackerListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        super(plugin, traitListenerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            checkLifeTracker(livingEntity);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            checkLifeTracker(livingEntity);
        }
    }

    /**
     * Check if an entity has its life tracked
     *
     * @param livingEntity The {@link LivingEntity} to check
     */
    private void checkLifeTracker(LivingEntity livingEntity) {
        final List<MetadataValue> values = livingEntity.getMetadata(plugin.getCustomKeys().TRACK_LIFE.getKey());
        if (values.isEmpty()) {
            return;
        }
        if (values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .anyMatch(MetadataValue::asBoolean)
        ) {
            refreshLife(livingEntity);
        }
    }

    /**
     * Refresh the life of a {@link LivingEntity}
     *
     * @param livingEntity The {@link LivingEntity} to refresh
     */
    public void refreshLife(LivingEntity livingEntity) {
        final List<MetadataValue> values = livingEntity.getMetadata(plugin.getCustomKeys().CUSTOM_NAME.getKey());
        if (values.isEmpty()) {
            return;
        }
        final Optional<MetadataValue> customName = values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .findAny();

        if (customName.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> updateCustomName(livingEntity, customName.get().asString()));
    }

    /**
     * Refresh the custom name of a {@link LivingEntity} with its life values
     *
     * @param livingEntity The {@link LivingEntity} to update
     * @param customName   The custom name to apply
     */
    private void updateCustomName(LivingEntity livingEntity, String customName) {
        final double health = livingEntity.getHealth();
        String formattedCustomName = customName.replace("%health%", lifeFormat.format(health));
        final AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance != null) {
            final double maxHealth = instance.getValue();
            formattedCustomName = formattedCustomName
                    .replace("%max-health%", maxLifeFormat.format(maxHealth))
                    .replace("%health-percentage%", percentageFormat.format(health / maxHealth * 100));
        }

        livingEntity.setCustomName(formattedCustomName);
    }

}
