package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class KnockUpListener extends TraitListener {

    public KnockUpListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        super(plugin, traitListenerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onKnock(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        if (damagerEntity instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooterEntity) {
            damagerEntity = shooterEntity;
        }
        final List<MetadataValue> values = damagerEntity.getMetadata(plugin.getCustomKeys().KNOCK_UP.getKey());
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
