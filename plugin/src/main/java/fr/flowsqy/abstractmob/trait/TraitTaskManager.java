package fr.flowsqy.abstractmob.trait;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.event.EntityPostLoadEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TraitTaskManager implements Listener {

    private final TraitLauncherTask traitLauncherTask;
    private final List<BukkitTaskLoader> taskLoaders;

    public TraitTaskManager(AbstractMobPlugin plugin) {
        this.traitLauncherTask = plugin.getTraitLauncherTask();
        taskLoaders = new LinkedList<>();
    }

    public void registerTask(BukkitTaskLoader taskLoader) {
        taskLoaders.add(taskLoader);
    }

    public void clear() {
        taskLoaders.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityLoad(EntityPostLoadEvent event) {
        loadEntities(event.getEntities());
    }

    public void loadEntities(Entity... entities) {
        Objects.requireNonNull(entities);
        for (BukkitTaskLoader taskLoader : taskLoaders) {
            traitLauncherTask.loadTaskEntities(taskLoader, entities);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChuckUnload(ChunkUnloadEvent event) {
        traitLauncherTask.unloadEntities(event.getChunk().getEntities());
    }

}
