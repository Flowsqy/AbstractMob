package fr.flowsqy.abstractmob.entity;

import org.bukkit.entity.Entity;

import java.util.List;
import java.util.function.Consumer;

public class EntityPropertyList<T> {

    private final Class<T> clazz;
    private final List<Consumer<T>> properties;

    public EntityPropertyList(Class<T> clazz, List<Consumer<T>> properties) {
        this.clazz = clazz;
        this.properties = properties;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public List<Consumer<T>> getProperties() {
        return properties;
    }

    public EntityPropertyList<T> add(Consumer<T> consumer) {
        getProperties().add(consumer);
        return this;
    }

    public void loadEntity(Entity entity) {
        final T typedEntity = clazz.cast(entity);
        for (Consumer<T> consumer : properties) {
            consumer.accept(typedEntity);
        }
    }

}
