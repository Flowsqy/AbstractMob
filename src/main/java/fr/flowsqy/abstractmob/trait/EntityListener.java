package fr.flowsqy.abstractmob.trait;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.Keys;
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
import java.util.Random;

public class EntityListener implements Listener {

    private final AbstractMobPlugin plugin;
    private final Random random;

    public EntityListener(AbstractMobPlugin plugin) {
        this.plugin = plugin;
        random = new Random();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final List<MetadataValue> values = entity.getMetadata(Keys.LIGHTNING_ON_DEATH.getKey());
        if (values.isEmpty()) {
            return;
        }
        final Optional<MetadataValue> pluginValue = values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .findAny();
        if (pluginValue.isEmpty()) {
            return;
        }
        final int chances = pluginValue.get().asInt();
        // Check chances and fire the lightning
        if (chances == 0 || random.nextInt(100_000) < chances) {
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
        final List<MetadataValue> values = damagerEntity.getMetadata(Keys.KNOCKBACK_UP.getKey());
        if (values.isEmpty()) {
            return;
        }
        double up = 0;
        for (MetadataValue value : values) {
            up += value.asDouble();
        }
        event.getEntity().setVelocity(event.getEntity().getVelocity().setY(up / values.size()));
    }


}
