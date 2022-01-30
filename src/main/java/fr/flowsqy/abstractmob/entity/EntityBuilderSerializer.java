package fr.flowsqy.abstractmob.entity;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.CustomKeys;
import fr.flowsqy.abstractmob.trait.ChancesChecker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class EntityBuilderSerializer {

    // Utility class
    private EntityBuilderSerializer() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * Deserialize an EntityBuilder from a configuration section
     *
     * @param plugin  The plugin instance used to get custom keys, set metadata and launch custom tasks
     * @param section The section to deserialize
     * @return An {@link EntityBuilder} described by the specified section
     */
    public static EntityBuilder deserialize(AbstractMobPlugin plugin, ConfigurationSection section) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(section);
        final String entityTypeRaw = section.getString("type");
        final EntityType entityType = getEnumConstant(EntityType.class, entityTypeRaw);
        final Class<? extends Entity> entityClass;
        if (entityType == null || entityType.equals(EntityType.UNKNOWN) || (entityClass = entityType.getEntityClass()) == null) {
            return null;
        }
        final EntityBuilder builder = new EntityBuilder(entityClass, section.getInt("quantity", 1));

        // Edit properties
        final CustomKeys customKeys = plugin.getCustomKeys();

        // Entity properties
        final ConfigurationSection baseSection = section.getConfigurationSection("base");
        if (baseSection != null) {
            final EntityPropertyList<Entity> entityPropertyList = builder.getOrRegisterModifiers(Entity.class);

            final int lightningChances = clamp(baseSection.getInt("lightning-on-death", 0), 100, 0);
            if (lightningChances != 0) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.LIGHTNING_ON_DEATH.getKey(),
                                new FixedMetadataValue(plugin, ChancesChecker.classicToPlugin(lightningChances))
                        ));
            }

            final int spiderwebChances = clamp(baseSection.getInt("web-on-walk", 0), 100, 0);
            if (spiderwebChances != 0) {
                entityPropertyList
                        .add(entity -> {
                                    entity.setMetadata(
                                            customKeys.WEB_ON_WALK.getKey(),
                                            new FixedMetadataValue(plugin, ChancesChecker.classicToPlugin(spiderwebChances))
                                    );
                                    plugin.getTraitLauncherTask().loadEntities(plugin.getSpiderWebTaskLoader(), entity);
                                }
                        );

            }

            final double knockbackUp = baseSection.getDouble("knockback-up", 0);
            if (knockbackUp != 0) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.KNOCKBACK_UP.getKey(),
                                new FixedMetadataValue(plugin, knockbackUp)
                        ));
            }

            entityPropertyList.add(entity -> plugin.getUpdateTask().saveEntities(entity));
        }

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

    private static int clamp(int value, int maximum, int minimum) {
        if (value > maximum) {
            return maximum;
        }
        return Math.max(value, minimum);
    }

}
