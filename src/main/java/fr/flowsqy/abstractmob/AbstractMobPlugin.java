package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.keys.Keys;
import fr.flowsqy.abstractmob.updater.KeyUpdater;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractMobPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        KeyUpdater.Commons.init(this);
        Keys.initKeys(this);
    }

}