package fr.flowsqy.abstractmob.updater;

import fr.flowsqy.abstractmob.key.Keys;
import fr.flowsqy.abstractmob.thread.ThreadedTask;
import org.bukkit.entity.Entity;

public class UpdaterTask extends ThreadedTask {

    public UpdaterTask() {
        super("Entity Updater");
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
