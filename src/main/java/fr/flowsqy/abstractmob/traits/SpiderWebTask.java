package fr.flowsqy.abstractmob.traits;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SpiderWebTask extends BukkitRunnable {

    private final Entity entity;
    private final int chances;
    private final Random random;
    private int px, py, pz;

    public SpiderWebTask(Entity entity, int chances) {
        if (chances <= 0) {
            throw new IllegalArgumentException("Useless spider task for a spawn probability of 0");
        }
        this.entity = entity;
        if (chances >= 100) {
            this.chances = 0;
            random = null;
        } else {
            this.chances = chances * 1000;
            random = new Random();
        }
    }

    @Override
    public void run() {
        // Stop if the entity is alive
        if (!entity.isValid()) {
            cancel();
            return;
        }

        // Check same location
        final Location location = entity.getLocation();
        final int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        if (x == px && y == py && z == pz) {
            return;
        }
        // Set future previous position to current location
        px = x;
        py = y;
        pz = z;

        // Check chances and place the cobweb
        if (chances == 0 || random.nextInt(100_000) < this.chances) {
            entity.getWorld().getBlockAt(px, py, pz).setType(Material.COBWEB);
        }
    }

    public void start(AbstractMobPlugin plugin) {
        runTaskTimer(plugin, 0L, 1L);
    }

}
