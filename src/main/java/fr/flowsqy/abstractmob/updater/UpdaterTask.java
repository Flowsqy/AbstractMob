package fr.flowsqy.abstractmob.updater;

import fr.flowsqy.abstractmob.key.CustomKey;
import fr.flowsqy.abstractmob.key.CustomKeys;
import fr.flowsqy.abstractmob.thread.ThreadedTask;
import org.bukkit.entity.Entity;

public class UpdaterTask extends ThreadedTask {

    private final CustomKeys customKeys;

    public UpdaterTask(CustomKeys customKeys) {
        super("Entity Updater");
        this.customKeys = customKeys;
    }

    public void saveEntities(Entity... entities) {
        queue(() -> {
            for (CustomKey key : customKeys.getKeys()) {
                key.getUpdater().save(key, entities);
            }
        });
    }

    public void loadEntities(Entity... entities) {
        queue(() -> {
            for (CustomKey key : customKeys.getKeys()) {
                key.getUpdater().load(key, entities);
            }
        });
    }

}
