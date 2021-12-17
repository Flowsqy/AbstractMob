package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.thread.EntityUpdaterRunnable;
import fr.flowsqy.abstractmob.thread.IterationRunnable;
import fr.flowsqy.abstractmob.thread.VelocityRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractMobPlugin extends JavaPlugin {

    public final static String LIGHTING_ON_DEATH = "lightning_on_death";
    public final static String KNOCKBACK = "knockback";
    public final NamespacedKey LIGHTING_ON_DEATH_KEY = new NamespacedKey(this, LIGHTING_ON_DEATH);

    private IterationRunnable<Entity> entityUpdaterRunnable;
    private IterationRunnable<EntityDamageByEntityEvent> velocityRunnable;

    @Override
    public void onEnable() {
        entityUpdaterRunnable = new EntityUpdaterRunnable(50, LIGHTING_ON_DEATH_KEY, new FixedMetadataValue(this, true));
        velocityRunnable = new VelocityRunnable(50);

        velocityRunnable.runTaskTimerAsynchronously(this, 0L, 1L);

        new TestListener(this, entityUpdaterRunnable, velocityRunnable);
        for(World world : Bukkit.getWorlds()){
            for(Chunk chunk : world.getLoadedChunks()){
                entityUpdaterRunnable.add(chunk.getEntities());
            }
        }
    }

    @Override
    public void onDisable() {
        entityUpdaterRunnable.cancel();
        velocityRunnable.cancel();
    }
}