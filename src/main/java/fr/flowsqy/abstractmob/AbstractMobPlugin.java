package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.key.CustomKeys;
import fr.flowsqy.abstractmob.trait.ChancesChecker;
import fr.flowsqy.abstractmob.trait.TraitLauncherTask;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import fr.flowsqy.abstractmob.trait.internal.TraitTaskListener;
import fr.flowsqy.abstractmob.trait.internal.listener.InternalListeners;
import fr.flowsqy.abstractmob.trait.internal.SpiderWebTaskLoader;
import fr.flowsqy.abstractmob.updater.KeyUpdaters;
import fr.flowsqy.abstractmob.updater.UpdateListener;
import fr.flowsqy.abstractmob.updater.UpdaterTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class AbstractMobPlugin extends JavaPlugin {

    private KeyUpdaters keyUpdaters;
    private CustomKeys customKeys;
    private UpdaterTask updateTask;
    private Random random;
    private ChancesChecker chancesChecker;
    private SpiderWebTaskLoader spiderWebTaskLoader;
    private TraitLauncherTask traitLauncherTask;
    private TraitListenerManager traitListenerManager;

    @Override
    public void onEnable() {
        // Init keys
        keyUpdaters = new KeyUpdaters(this);
        customKeys = new CustomKeys(this);

        // Create trait tasks and task loaders
        traitLauncherTask = new TraitLauncherTask();
        spiderWebTaskLoader = new SpiderWebTaskLoader(this);
        Bukkit.getPluginManager().registerEvents(new TraitTaskListener(this), this);
        traitLauncherTask.start();

        // Create update task, link it with listener and launch it
        updateTask = new UpdaterTask(customKeys);
        final UpdateListener updateListener = new UpdateListener(updateTask);
        Bukkit.getPluginManager().registerEvents(updateListener, this);
        updateListener.loadSpawnChunks();
        updateTask.start();

        // Register traits
        random = new Random();
        chancesChecker = new ChancesChecker(this);
        traitListenerManager = new TraitListenerManager();

        // Register internals traits
        new InternalListeners(this);

        // Run after all plugins initializations
        Bukkit.getScheduler().runTask(this, () -> {
            traitListenerManager.registerAll();
        });
    }

    @Override
    public void onDisable() {
        updateTask.stop();
        Bukkit.getScheduler().cancelTasks(this);
        traitListenerManager.unregisterAll();
        traitListenerManager.clear();
    }

    public KeyUpdaters getKeyUpdaters() {
        return keyUpdaters;
    }

    public CustomKeys getCustomKeys() {
        return customKeys;
    }

    public UpdaterTask getUpdateTask() {
        return updateTask;
    }

    public Random getRandom() {
        return random;
    }

    public ChancesChecker getChancesChecker() {
        return chancesChecker;
    }

    public TraitListenerManager getTraitListenerManager() {
        return traitListenerManager;
    }

    public SpiderWebTaskLoader getSpiderWebTaskLoader() {
        return spiderWebTaskLoader;
    }

    public TraitLauncherTask getTraitLauncherTask() {
        return traitLauncherTask;
    }
}