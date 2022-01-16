package fr.flowsqy.abstractmob.updater;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.Keys;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

public interface KeyUpdater {

    void load(Keys key, Entity... entities);

    void save(Keys key, Entity... entities);

    abstract class AbstractUpdater implements KeyUpdater {

        protected AbstractMobPlugin plugin;

        private void init(AbstractMobPlugin plugin) {
            if (this.plugin != null) {
                throw new IllegalStateException("Updater already initialized");
            }
            this.plugin = plugin;
        }

    }

    class BooleanUpdater extends AbstractUpdater {

        private BooleanUpdater() {
        }

        @Override
        public void load(Keys key, Entity... entities) {
            final MetadataValue trueValue = new FixedMetadataValue(plugin, true);
            for (Entity entity : entities) {
                final Byte value = entity.getPersistentDataContainer().get(key.getNamespacedKey(), PersistentDataType.BYTE);
                if (value != null && value == 1) {
                    entity.setMetadata(
                            key.getKey(),
                            trueValue
                    );
                }
            }
        }

        @Override
        public void save(Keys key, Entity... entities) {
            for (Entity entity : entities) {
                final List<MetadataValue> values = entity.getMetadata(key.getKey());
                if (values.isEmpty()) {
                    continue;
                }
                final Optional<MetadataValue> pluginValue = values.stream()
                        .filter(value -> value.getOwningPlugin() == plugin)
                        .findAny();
                if (pluginValue.isPresent() && pluginValue.get().asBoolean()) {
                    entity.getPersistentDataContainer().set(key.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1);
                } else {
                    entity.getPersistentDataContainer().remove(key.getNamespacedKey());
                }
            }
        }

    }

    abstract class NumberUpdater<T extends Number> extends AbstractUpdater {

        @Override
        public void load(Keys key, Entity... entities) {
            for (Entity entity : entities) {
                final T value = entity.getPersistentDataContainer().get(key.getNamespacedKey(), getDataType());
                if (value == null) {
                    entity.removeMetadata(key.getKey(), plugin);
                } else {
                    entity.setMetadata(key.getKey(), newValue(value));
                }
            }
        }

        @Override
        public void save(Keys key, Entity... entities) {
            for (Entity entity : entities) {
                final List<MetadataValue> values = entity.getMetadata(key.getKey());
                if (values.isEmpty()) {
                    continue;
                }
                final Optional<MetadataValue> pluginValue = values.stream()
                        .filter(value -> value.getOwningPlugin() == plugin)
                        .findAny();
                if (pluginValue.isPresent() && pluginValue.get().asBoolean()) {
                    entity.getPersistentDataContainer().set(key.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1);
                }
            }
        }

        protected abstract PersistentDataType<T, T> getDataType();

        protected abstract MetadataValue newValue(T value);

    }

    class IntegerUpdater extends NumberUpdater<Integer> {

        private IntegerUpdater() {
        }

        @Override
        protected PersistentDataType<Integer, Integer> getDataType() {
            return PersistentDataType.INTEGER;
        }

        @Override
        protected MetadataValue newValue(Integer value) {
            return new FixedMetadataValue(plugin, value);
        }
    }

    class DoubleUpdater extends NumberUpdater<Double> {

        private DoubleUpdater() {
        }

        @Override
        protected PersistentDataType<Double, Double> getDataType() {
            return PersistentDataType.DOUBLE;
        }

        @Override
        protected MetadataValue newValue(Double value) {
            return new FixedMetadataValue(plugin, value);
        }
    }

    class Commons {

        public final static AbstractUpdater BOOLEAN = new BooleanUpdater();
        public final static AbstractUpdater INTEGER = new IntegerUpdater();
        public final static AbstractUpdater DOUBLE = new DoubleUpdater();

        public static void init(AbstractMobPlugin plugin) {
            BOOLEAN.init(plugin);
            INTEGER.init(plugin);
            DOUBLE.init(plugin);
        }

    }

}
