package fr.flowsqy.abstractmob.thread;

import fr.flowsqy.abstractmob.Keys;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class VelocityRunnable implements Runnable {

    private final EntityDamageByEntityEvent event;

    public VelocityRunnable(EntityDamageByEntityEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        Entity damagerEntity = event.getDamager();
        if (damagerEntity instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooterEntity) {
            damagerEntity = shooterEntity;
        }
        final List<MetadataValue> values = damagerEntity.getMetadata(Keys.KNOCKBACK);
        if (values.isEmpty()) {
            return;
        }
        double up = 0;
        for(MetadataValue value : values){
            up += value.asDouble();
        }
        event.getEntity().setVelocity(event.getEntity().getVelocity().setY(up / values.size()));
    }
}
