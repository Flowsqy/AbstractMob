package fr.flowsqy.abstractmob.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.*;

public class CustomEntity {

    private final Class<? extends Entity> type;
    private final List<EntityPropertyList<?>> modifiers;
    private int quantity;
    private int lightningChances;
    private int spiderWebChances;
    private double knockBack;

    public CustomEntity(Class<? extends Entity> type, int quantity) {
        Objects.requireNonNull(type);
        this.type = type;
        setQuantity(quantity);
        modifiers = new ArrayList<>();
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
            final EntityPropertyList<T> newEntityPropertyList = new EntityPropertyList<>(clazz, new ArrayList<>());
            modifiers.add(newEntityPropertyList);
            return newEntityPropertyList;
        }
        return entityPropertyList.get();
    }

    public int getLightningChances() {
        return lightningChances;
    }

    public void setLightningChances(int lightningChances) {
        this.lightningChances = lightningChances;
    }

    public int getSpiderWebChances() {
        return spiderWebChances;
    }

    public void setSpiderWebChances(int spiderWebChances) {
        this.spiderWebChances = spiderWebChances;
    }

    public double getKnockBack() {
        return knockBack;
    }

    public void setKnockBack(double knockBack) {
        this.knockBack = knockBack;
    }

    public void spawn(Random random, Location location) {
        spawn(random, location, 0, false, quantity);
    }

    public void spawn(Random random, Location location, int radius) {
        spawn(random, location, radius, false, quantity);
    }

    public void spawn(Random random, Location location, int radius, boolean highestBlock) {
        spawn(random, location, radius, highestBlock, quantity);
    }

    public void spawn(Random random, Location location, int radius, boolean highestBlock, int quantity) {
        Objects.requireNonNull(random);
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
            world.spawn(spawnLocation, type, false, (entity -> modifiers.forEach(list -> list.loadEntity(entity))));
        }
    }

}
