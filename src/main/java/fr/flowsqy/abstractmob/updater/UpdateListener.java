package fr.flowsqy.abstractmob.updater;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class UpdateListener implements Listener {

    private final UpdaterTask updateTask;

    public UpdateListener(UpdaterTask updateTask) {
        this.updateTask = updateTask;
    }

    public void loadSpawnChunks() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                updateTask.loadEntities(chunk.getEntities());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) {
            return;
        }
        updateTask.loadEntities(event.getChunk().getEntities());
    }

}
