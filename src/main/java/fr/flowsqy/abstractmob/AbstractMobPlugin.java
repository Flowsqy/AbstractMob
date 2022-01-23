package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.key.Keys;
import fr.flowsqy.abstractmob.trait.ChancesChecker;
import fr.flowsqy.abstractmob.trait.EntityListener;
import fr.flowsqy.abstractmob.updater.KeyUpdater;
import fr.flowsqy.abstractmob.updater.UpdateListener;
import fr.flowsqy.abstractmob.updater.UpdaterTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class AbstractMobPlugin extends JavaPlugin {

    private UpdaterTask updateTask;
    private Random random;
    private ChancesChecker chancesChecker;

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

        // Register traits
        random = new Random();
        chancesChecker = new ChancesChecker(this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
    }

    @Override
    public void onDisable() {
        updateTask.stop();
        Bukkit.getScheduler().cancelTasks(this);
    }

    public Random getRandom() {
        return random;
    }

    public ChancesChecker getChancesChecker() {
        return chancesChecker;
    }
}