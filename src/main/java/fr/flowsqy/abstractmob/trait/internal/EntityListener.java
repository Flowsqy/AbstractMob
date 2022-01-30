package fr.flowsqy.abstractmob.trait.internal;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;

public class EntityListener implements Listener {

    private final AbstractMobPlugin plugin;

    public EntityListener(AbstractMobPlugin plugin) {
        this.plugin = plugin;
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

    @EventHandler(priority = EventPriority.MONITOR)
    private void onKnock(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        if (damagerEntity instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooterEntity) {
            damagerEntity = shooterEntity;
        }
        final List<MetadataValue> values = damagerEntity.getMetadata(plugin.getCustomKeys().KNOCKBACK_UP.getKey());
        if (values.isEmpty()) {
            return;
        }
        double up = 0;
        for (MetadataValue value : values) {
            up += value.asDouble();
        }
        final double upValue = up / values.size();

        Bukkit.getScheduler().runTask(plugin, () -> event.getEntity().setVelocity(event.getEntity().getVelocity().setY(upValue)));
    }


}
