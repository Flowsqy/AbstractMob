package fr.flowsqy.abstractmob.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.abstractmob.AbstractMobPlugin;

public class EntityBuilder {

    private final Class<? extends Entity> type;
    private final List<EntityPropertyList<?>> modifiers;
    private int quantity;
    private int radius;

    public EntityBuilder(Class<? extends Entity> type, int quantity, int radius) {
        Objects.requireNonNull(type);
        this.type = type;
        setQuantity(quantity);
        setRadius(radius);
        modifiers = new LinkedList<>();
    }

    public Class<? extends Entity> getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Can not set quantity to 0 or bellow");
        }
        this.quantity = quantity;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Can not set the spawn radius to a negative number");
        }
        this.radius = radius;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<EntityPropertyList<T>> getModifiers(Class<T> clazz) {
        final Optional<EntityPropertyList<?>> propertyList = modifiers.stream()
                .filter(entityPropertyList -> entityPropertyList.getClazz() == clazz)
                .findAny();
        return propertyList.map(entityPropertyList -> (EntityPropertyList<T>) entityPropertyList);
    }

    @NotNull
    public <T> EntityPropertyList<T> getOrRegisterModifiers(Class<T> clazz) {
        final Optional<EntityPropertyList<T>> entityPropertyList = getModifiers(clazz);
        if (entityPropertyList.isEmpty()) {
            if (!clazz.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type.getName() + " can not be cast to " + clazz.getName());
            }
            final EntityPropertyList<T> newEntityPropertyList = new EntityPropertyList<>(clazz, new LinkedList<>());
            modifiers.add(newEntityPropertyList);
            return newEntityPropertyList;
        }
        return entityPropertyList.get();
    }

    public List<Entity> spawn(AbstractMobPlugin plugin, Location location) {
        return spawn(plugin, location, radius, false, quantity);
    }

    public List<Entity> spawn(AbstractMobPlugin plugin, Location location, int radius) {
        return spawn(plugin, location, radius, false, quantity);
    }

    public List<Entity> spawn(AbstractMobPlugin plugin, Location location, int radius, boolean highestBlock) {
        return spawn(plugin, location, radius, highestBlock, quantity);
    }

    public List<Entity> spawn(AbstractMobPlugin plugin, Location location, int radius, boolean highestBlock, int quantity) {
        return spawn(plugin, location, radius, highestBlock, quantity, plugin.getRandom());
    }

    /**
     * Spawn a custom entity
     *
     * @param plugin       The plugin instance to launch custom task
     * @param location     The base location where entity must spawn
     * @param radius       The radius of the 'location' center circle where entities will spawn randomly
     * @param highestBlock Whether the entities must spawn at the highest block or at the y coordinate specified in 'location'
     * @param quantity     The quantity of entity to spawn
     * @param random       The random instance to use to randomly spawn entities in the radius. Can be {@code null} if the radius is equals to zero
     * @return A {@link List} of the spawned entities
     */
    public List<Entity> spawn(AbstractMobPlugin plugin, Location location, int radius, boolean highestBlock, int quantity, Random random) {
        Objects.requireNonNull(plugin);
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity can not be below 1");
        }
        final World world = location.getWorld();
        Objects.requireNonNull(world);
        final List<Entity> entities = new LinkedList<>();

        final int doubledRadius = radius * 2;
        for (int i = 0; i < quantity; i++) {
            Location spawnLocation = location;
            if (radius > 0) {
                final Vector vector = new Vector(
                        random.nextInt(doubledRadius) - radius,
                        0,
                        random.nextInt(doubledRadius) - radius
                );
                vector.normalize().multiply(radius * random.nextDouble());
                spawnLocation = spawnLocation.clone().add(vector);
            }
            if (highestBlock) {
                spawnLocation = world.getHighestBlockAt(spawnLocation).getLocation().add(0, 1, 0);
            }
            final Entity spawnedEntity = world.spawn(
                    spawnLocation,
                    type,
                    false,
                    entity -> modifiers.forEach(list -> list.loadEntity(entity))
            );
            entities.add(spawnedEntity);
        }

        final Entity[] entityArray = entities.toArray(new Entity[0]);

        // Load entity tasks
        plugin.getTraitTaskManager().loadEntities(entityArray);

        // Save every entity traits in the permanent storage
        plugin.getUpdateTask().saveEntities(entityArray);

        return entities;
    }

}
