package fr.flowsqy.abstractmob.trait;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.event.EntityPostLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class TraitTaskListener implements Listener {

    private final TraitLauncherTask traitLauncherTask;
    private final SpiderWebTaskLoader spiderWebTaskLoader;

    public TraitTaskListener(AbstractMobPlugin plugin) {
        this.traitLauncherTask = plugin.getTraitLauncherTask();
        this.spiderWebTaskLoader = plugin.getSpiderWebTaskLoader();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityLoad(EntityPostLoadEvent event) {
        traitLauncherTask.loadEntities(spiderWebTaskLoader, event.getEntities());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChuckUnload(ChunkUnloadEvent event) {
        traitLauncherTask.unloadEntities(event.getChunk().getEntities());
    }

}
