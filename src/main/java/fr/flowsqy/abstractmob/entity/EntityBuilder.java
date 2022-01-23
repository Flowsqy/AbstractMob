package fr.flowsqy.abstractmob.entity;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.*;

public class EntityBuilder {

    private final Class<? extends Entity> type;
    private final List<EntityPropertyList<?>> modifiers;
    private int quantity;

    public EntityBuilder(Class<? extends Entity> type, int quantity) {
        Objects.requireNonNull(type);
        this.type = type;
        setQuantity(quantity);
        modifiers = new LinkedList<>();
    }

    /**
     * Deserialize an EntityBuilder from a configuration section
     *
     * @param section The section to deserialize
     * @return An {@link EntityBuilder} described by the specified section
     */
    public static EntityBuilder deserialize(ConfigurationSection section) {
        Objects.requireNonNull(section);
        final String entityTypeRaw = section.getString("type");
        final EntityType entityType = getEnumConstant(EntityType.class, entityTypeRaw);
        final Class<? extends Entity> entityClass;
        if (entityType == null || entityType.equals(EntityType.UNKNOWN) || (entityClass = entityType.getEntityClass()) == null) {
            return null;
        }
        final EntityBuilder builder = new EntityBuilder(entityClass, section.getInt("quantity", 1));

        // TODO Add properties

        return builder;
    }

    private static <T extends Enum<T>> T getEnumConstant(Class<T> enumClass, String value) {
        if (enumClass == null || value == null)
            return null;

        value = value.trim().toUpperCase();

        for (final T constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(value))
                return constant;
        }

        return null;
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

    @SuppressWarnings("unchecked")
    public <T> Optional<EntityPropertyList<T>> getModifiers(Class<T> clazz) {
        final Optional<EntityPropertyList<?>> propertyList = modifiers.stream()
                .filter(entityPropertyList -> entityPropertyList.getClazz() == clazz)
                .findAny();
        return propertyList.map(entityPropertyList -> (EntityPropertyList<T>) entityPropertyList);
    }

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

    public void spawn(AbstractMobPlugin plugin, Location location) {
        spawn(plugin, location, 0, false, quantity);
    }

    public void spawn(AbstractMobPlugin plugin, Location location, int radius) {
        spawn(plugin, location, radius, false, quantity);
    }

    public void spawn(AbstractMobPlugin plugin, Location location, int radius, boolean highestBlock) {
        spawn(plugin, location, radius, highestBlock, quantity);
    }

    public void spawn(AbstractMobPlugin plugin, Location location, int radius, boolean highestBlock, int quantity) {
        spawn(plugin, location, radius, highestBlock, quantity, plugin.getRandom());
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
     */
    public void spawn(AbstractMobPlugin plugin, Location location, int radius, boolean highestBlock, int quantity, Random random) {
        Objects.requireNonNull(plugin);
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity can not be below 1");
        }
        final World world = location.getWorld();
        Objects.requireNonNull(world);

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
            world.spawn(
                    spawnLocation,
                    type,
                    false,
                    entity -> modifiers.forEach(list -> list.loadEntity(entity))
            );
        }
    }

}
