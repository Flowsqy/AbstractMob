package fr.flowsqy.abstractmob.trait;

import fr.flowsqy.abstractmob.thread.ThreadedTask;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TraitLauncherTask extends ThreadedTask {

    private final ConcurrentMap<UUID, ConcurrentMap<String, BukkitTask>> tasks;

    public TraitLauncherTask() {
        super("Task Launcher");
        tasks = new ConcurrentHashMap<>();
    }

    @Override
    public void stop() {
        super.stop();
        for (ConcurrentMap<String, BukkitTask> entityTasks : tasks.values()) {
            for (BukkitTask task : entityTasks.values()) {
                task.cancel();
            }
        }
        tasks.clear();
    }

    public void loadEntities(BukkitTaskLoader taskLoader, Entity... entities) {
        queue(() -> {
            for (Entity entity : entities) {
                final String identifier = taskLoader.getIdentifier();
                if (identifier != null) {
                    final BukkitTask task = taskLoader.start(entity);
                    if (task != null) {
                        final ConcurrentMap<String, BukkitTask> entityTasks = tasks.getOrDefault(entity.getUniqueId(), new ConcurrentHashMap<>());
                        final BukkitTask previousTask = entityTasks.put(identifier, task);
                        if (previousTask != null) {
                            previousTask.cancel();
                        }
                    }
                }
            }
        });
    }

    public void unloadEntities(Entity... entities) {
        queue(() -> {
            for (Entity entity : entities) {
                final ConcurrentMap<String, BukkitTask> entityTasks = tasks.remove(entity.getUniqueId());
                if (entityTasks != null) {
                    for (BukkitTask task : entityTasks.values()) {
                        task.cancel();
                    }
                }
            }
        });
    }

}
