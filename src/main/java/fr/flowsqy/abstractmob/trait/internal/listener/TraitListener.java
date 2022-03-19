package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class TraitListener implements Listener {

    protected final AbstractMobPlugin plugin;

    public TraitListener(AbstractMobPlugin plugin, TraitListenerManager traitListenerManager) {
        this.plugin = plugin;
        traitListenerManager.add(this);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
