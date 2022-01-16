package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.keys.Keys;
import fr.flowsqy.abstractmob.updater.KeyUpdater;
import fr.flowsqy.abstractmob.updater.UpdateListener;
import fr.flowsqy.abstractmob.updater.UpdaterTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractMobPlugin extends JavaPlugin {

    private UpdaterTask updateTask;

    @Override
    public void onEnable() {
        // Init keys
        KeyUpdater.Commons.init(this);
        Keys.initKeys(this);

        // Create update task, link it with listener and launch it
        updateTask = new UpdaterTask();
        final UpdateListener updateListener = new UpdateListener(updateTask);
        Bukkit.getPluginManager().registerEvents(updateListener, this);
        updateListener.loadSpawnChunks();
        updateTask.start();
    }

    @Override
    public void onDisable() {
        updateTask.stop();
        Bukkit.getScheduler().cancelTasks(this);
    }
}