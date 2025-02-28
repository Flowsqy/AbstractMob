package fr.flowsqy.abstractmob.trait.internal.task;

import fr.flowsqy.abstractmob.trait.ChancesChecker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class SpiderWebTask extends BukkitRunnable {

    private final Entity entity;
    private final ChancesChecker chancesChecker;
    private final int chances;
    private int px, py, pz;

    public SpiderWebTask(Entity entity, ChancesChecker chancesChecker, int chances) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(chancesChecker);
        if (chances < 0) {
            throw new IllegalArgumentException("Useless spider task for a negative spawn probability");
        }
        if (chances >= 100_000) {
            throw new IllegalArgumentException("Wrong value for spider task, chance must be between 0 and 99 * 10E5");
        }
        this.entity = entity;
        this.chancesChecker = chancesChecker;
        this.chances = chances;
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
        if (chancesChecker.canPerform(chances)) {
            final Block block = entity.getWorld().getBlockAt(px, py, pz);
            if (block.isPassable()) {
                block.setType(Material.COBWEB);
            }
        }
    }

}
