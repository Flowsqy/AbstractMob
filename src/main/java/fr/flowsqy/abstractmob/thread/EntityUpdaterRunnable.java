package fr.flowsqy.abstractmob.thread;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

public class EntityUpdaterRunnable extends IterationRunnable<Entity> {

    // CONSTANT
    private final NamespacedKey key;
    private final MetadataValue trueValue;


    public EntityUpdaterRunnable(int iterations, NamespacedKey key, MetadataValue trueValue) {
        super(iterations);
        this.key = key;
        this.trueValue = trueValue;
    }

    @Override
    protected void perform(Entity entity) {
        final Byte value = entity.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        if (value != null && value == 1) {
            entity.setMetadata(
                    AbstractMobPlugin.LIGHTING_ON_DEATH,
                    trueValue
            );
        }
    }

}
