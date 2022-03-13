package fr.flowsqy.abstractmob.updater;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.CustomKey;
import fr.flowsqy.abstractmob.trait.ChancesChecker;
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
    void load(CustomKey key, Entity... entities);

    /**
     * Save entities metadata in persistent storage for a specific key
     *
     * @param key      The key to save
     * @param entities The entities to update
     */
    void save(CustomKey key, Entity... entities);

    abstract class AbstractUpdater implements KeyUpdater {

        protected final AbstractMobPlugin plugin;

        protected AbstractUpdater(AbstractMobPlugin plugin) {
            this.plugin = plugin;
        }
    }

    class BooleanUpdater extends AbstractUpdater {

        BooleanUpdater(AbstractMobPlugin plugin) {
            super(plugin);
        }

        @Override
        public void load(CustomKey key, Entity... entities) {
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
        public void save(CustomKey key, Entity... entities) {
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

        protected NumberUpdater(AbstractMobPlugin plugin) {
            super(plugin);
        }

        @Override
        public void load(CustomKey key, Entity... entities) {
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
        public void save(CustomKey key, Entity... entities) {
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

        IntegerUpdater(AbstractMobPlugin plugin) {
            super(plugin);
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

        DoubleUpdater(AbstractMobPlugin plugin) {
            super(plugin);
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

        ChancesUpdater(AbstractMobPlugin plugin) {
            super(plugin);
        }

        @Override
        protected MetadataValue loadValue(Integer value) {
            return super.loadValue(ChancesChecker.classicToPlugin(value));
        }

        @Override
        protected Integer saveValue(MetadataValue value) {
            return ChancesChecker.pluginToClassic(super.saveValue(value));
        }
    }

}
