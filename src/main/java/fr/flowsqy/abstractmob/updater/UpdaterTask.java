package fr.flowsqy.abstractmob.updater;

import fr.flowsqy.abstractmob.keys.Keys;
import org.bukkit.entity.Entity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UpdaterTask {

    private final BlockingQueue<Runnable> queue;
    private volatile Thread thread;

    public UpdaterTask() {
        queue = new LinkedBlockingQueue<>();
    }

    /**
     * Start task, except if it is already
     */
    public void start() {
        if (!isRunning()) {
            thread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        queue.take().run();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "Entity Updater");
            thread.start();
        }
    }

    /**
     * Stop any running task then start it again
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Stop task properly
     */
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * @return whether task is running or not
     */
    public boolean isRunning() {
        return thread != null;
    }

    /**
     * Register a task to run before next loop
     *
     * @param runnable task to run
     */
    public void queue(Runnable runnable) {
        queue.add(runnable);
    }

    public void saveEntities(Entity... entities) {
        queue(() -> {
            for (Keys key : Keys.values()) {
                key.getUpdater().save(key, entities);
            }
        });
    }

    public void loadEntities(Entity... entities) {
        queue(() -> {
            for (Keys key : Keys.values()) {
                key.getUpdater().load(key, entities);
            }
        });
    }

}
