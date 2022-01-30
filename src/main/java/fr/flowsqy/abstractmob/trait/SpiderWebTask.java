package fr.flowsqy.abstractmob.trait;

import org.bukkit.Location;
import org.bukkit.Material;
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
            entity.getWorld().getBlockAt(px, py, pz).setType(Material.COBWEB);
        }
    }

}
