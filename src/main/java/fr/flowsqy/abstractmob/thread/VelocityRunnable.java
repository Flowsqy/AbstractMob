package fr.flowsqy.abstractmob.thread;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

public class VelocityRunnable extends IterationRunnable<EntityDamageByEntityEvent> {

    public VelocityRunnable(int iterations) {
        super(iterations);
    }

    @Override
    protected void perform(EntityDamageByEntityEvent event) {

        /*
        int vectorCount = 0;
        final Vector multiplier = new Vector();
        for(MetadataValue value : values){
            if(value.value() instanceof Vector vector){
                multiplier.add(vector);
                vectorCount++;
            }
        }

        final Location damagerLocation = damagerEntity.getLocation();
        final Location targetedLocation = event.getEntity().getLocation();
        final Vector vector = new Vector(
                targetedLocation.getX() - damagerLocation.getX(),
                0,
                targetedLocation.getZ() - damagerLocation.getZ()
        );
        if(vector.getX() != 0 || vector.getZ() != 0){
            vector.normalize();
        }
        vector.setY(1);
        if(vectorCount > 0){
            multiplier.multiply(1d / vectorCount);
            vector.multiply(multiplier);
        }
        event.getEntity().setVelocity(vector);
        */

    }
}
