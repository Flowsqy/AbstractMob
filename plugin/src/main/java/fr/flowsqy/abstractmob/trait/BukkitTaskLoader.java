package fr.flowsqy.abstractmob.trait;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public interface BukkitTaskLoader {

    /**
     * Start a {@link BukkitTask} for this entity
     *
     * @param entity The entity associated to the task
     * @return The started {@link BukkitTask}
     */
    BukkitTask start(Entity entity);

    /**
     * Get a unique {@link String} identifier for this type of {@link BukkitTask}
     *
     * @return The {@link String} associated with this type of {@link BukkitTask}
     */
    String getIdentifier();

}
