package fr.flowsqy.abstractmob.thread;

import fr.flowsqy.abstractmob.Keys;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class EntityUpdaterRunnable implements Runnable {

    private final Entity[] entities;

    public EntityUpdaterRunnable(Entity[] entities) {
        this.entities = entities;
    }

    @Override
    public void run() {
        for(Entity entity : entities) {
            final Byte value = entity.getPersistentDataContainer().get(Keys.LIGHTING_ON_DEATH_KEY, PersistentDataType.BYTE);
            if (value != null && value == 1) {
                entity.setMetadata(
                        Keys.LIGHTING_ON_DEATH,
                        Keys.TRUE_VALUE
                );
            }
        }
    }
}
