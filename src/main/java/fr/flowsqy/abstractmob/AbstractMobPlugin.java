package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.thread.EntityUpdaterRunnable;
import fr.flowsqy.abstractmob.thread.IterationTask;
import fr.flowsqy.abstractmob.thread.VelocityRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractMobPlugin extends JavaPlugin {

    /*
    private IterationRunnable<Entity> entityUpdaterRunnable;
    private IterationRunnable<EntityDamageByEntityEvent> velocityRunnable;
     */

    private IterationTask entityUpdaterTask;

    @Override
    public void onEnable() {
        Keys.init(this);
        /*
        entityUpdaterRunnable = new EntityUpdaterRunnable(50, LIGHTING_ON_DEATH_KEY, new FixedMetadataValue(this, true));
        velocityRunnable = new VelocityRunnable(50);
        */

        //velocityRunnable.runTaskTimerAsynchronously(this, 0L, 1L);

        entityUpdaterTask = new IterationTask("Entity Updater");
        entityUpdaterTask.start();

        new TestListener(this);
        for(World world : Bukkit.getWorlds()){
            for(Chunk chunk : world.getLoadedChunks()){
                entityUpdaterTask.queue(new EntityUpdaterRunnable(chunk.getEntities()));
            }
        }
    }

    @Override
    public void onDisable() {
        entityUpdaterTask.stop();
    }

    public IterationTask getEntityUpdaterTask() {
        return entityUpdaterTask;
    }
}