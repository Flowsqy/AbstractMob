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

    /**
     * Load entities metadata from persistent storage for a specific key
     *
     * @param key      The key to load
     * @param entities The entities to update
     */
    void load(Keys key, Entity... entities);

    /**
     * Save entities metadata in persistent storage for a specific key
     *
     * @param key      The key to save
     * @param entities The entities to update
     */
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
                } else if (!value.equals(0)) {
                    // Don't take 0 values as it represents 'no value'
                    // Handle an error if the value can not be converted
                    try {
                        entity.setMetadata(key.getKey(), loadValue(value));
                    } catch (Exception e) {
                        //TODO Handle the error in debugging
                    }
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
                if (pluginValue.isEmpty()) {
                    continue;
                }
                final T value;
                try {
                    value = saveValue(pluginValue.get());
                } catch (Exception e) {
                    // TODO Handle the error in debugging
                    return;
                }
                if (!value.equals(0)) {
                    entity.getPersistentDataContainer().set(key.getNamespacedKey(), getDataType(), value);
                }
            }
        }

        protected abstract PersistentDataType<T, T> getDataType();

        protected abstract MetadataValue loadValue(T value);

        protected abstract T saveValue(MetadataValue value);

    }

    class IntegerUpdater extends NumberUpdater<Integer> {

        private IntegerUpdater() {
        }

        @Override
        protected PersistentDataType<Integer, Integer> getDataType() {
            return PersistentDataType.INTEGER;
        }

        @Override
        protected MetadataValue loadValue(Integer value) {
            return new FixedMetadataValue(plugin, value);
        }

        @Override
        protected Integer saveValue(MetadataValue value) {
            return value.asInt();
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
        protected MetadataValue loadValue(Double value) {
            return new FixedMetadataValue(plugin, value);
        }

        @Override
        protected Double saveValue(MetadataValue value) {
            return value.asDouble();
        }

    }

    class ChancesUpdater extends IntegerUpdater {

        @Override
        protected MetadataValue loadValue(Integer value) {
            if (value < 0) {
                throw new IllegalArgumentException("The chances can not be bellow 0");
            }
            return super.loadValue(value >= 100 ? 0 : value * 1000);
        }

        @Override
        protected Integer saveValue(MetadataValue value) {
            final int metadataValue = super.saveValue(value);
            if (metadataValue < 0) {
                throw new IllegalArgumentException("The chances can not be bellow 0");
            }
            return metadataValue == 0 ? 100 : metadataValue / 1000;
        }
    }

    class Commons {

        public final static AbstractUpdater BOOLEAN = new BooleanUpdater();
        public final static AbstractUpdater INTEGER = new IntegerUpdater();
        public final static AbstractUpdater DOUBLE = new DoubleUpdater();
        public final static AbstractUpdater CHANCES = new ChancesUpdater();

        public static void init(AbstractMobPlugin plugin) {
            BOOLEAN.init(plugin);
            INTEGER.init(plugin);
            DOUBLE.init(plugin);
            CHANCES.init(plugin);
        }

    }

}
