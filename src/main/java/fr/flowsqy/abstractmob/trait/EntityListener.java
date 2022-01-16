package fr.flowsqy.abstractmob.trait;

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

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        if (entity.hasMetadata(Keys.LIGHTNING_ON_DEATH.getKey())) {
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
