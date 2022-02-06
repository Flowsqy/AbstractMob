package fr.flowsqy.abstractmob.entity;

import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.CustomKeys;
import fr.flowsqy.abstractmob.trait.ChancesChecker;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
                                    plugin.getTraitLauncherTask().loadTaskEntities(plugin.getSpiderWebTaskLoader(), entity);
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

            final boolean projectileResistance = baseSection.getBoolean("projectile-resistance", false);
            if (projectileResistance) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.PROJECTILE_RESISTANCE.getKey(),
                                new FixedMetadataValue(plugin, true)
                        ));
            }
        }

        // Attribute properties
        final ConfigurationSection attributesSection = section.getConfigurationSection("attribute");
        if (attributesSection != null) {
            final EntityPropertyList<Attributable> attributablePropertyList = builder.getOrRegisterModifiers(Attributable.class);

            for (String attributeKey : attributesSection.getKeys(false)) {
                final Attribute attribute = getEnumConstant(Attribute.class, attributeKey);
                if (attribute == null) {
                    continue;
                }
                final ConfigurationSection attributeSection = attributesSection.getConfigurationSection(attributeKey);
                if (attributeSection == null) {
                    continue;
                }
                final double value = attributeSection.getDouble("value", 0d);
                if (value == 0) {
                    continue;
                }
                final EquipmentSlot slot = getEnumConstant(EquipmentSlot.class, attributeSection.getString("slot"));
                final AttributeModifier.Operation operation = getEnumConstant(AttributeModifier.Operation.class, attributeSection.getString("operation"));
                attributablePropertyList.add(new Consumer<>() {
                    @Override
                    public void accept(Attributable attributable) {
                        final AttributeInstance instance = attributable.getAttribute(attribute);
                        if (instance == null) {
                            attributablePropertyList.getProperties().remove(this);
                            return;
                        }
                        instance.addModifier(new AttributeModifier(
                                UUID.randomUUID(),
                                "AbstractMob-Serialized-" + attribute.name(),
                                value,
                                operation == null ? AttributeModifier.Operation.ADD_NUMBER : operation,
                                slot
                        ));
                    }
                });
            }
        }

        // Equipment properties
        final ConfigurationSection equipmentSection = section.getConfigurationSection("equipment");
        if (equipmentSection != null) {
            final EntityPropertyList<LivingEntity> livingPropertyList = builder.getOrRegisterModifiers(LivingEntity.class);

            initEquipment(
                    equipmentSection,
                    "main-hand",
                    livingPropertyList,
                    EntityEquipment::setItemInMainHand,
                    EntityEquipment::setItemInMainHandDropChance)
            ;

            initEquipment(
                    equipmentSection,
                    "off-hand",
                    livingPropertyList,
                    EntityEquipment::setItemInOffHand,
                    EntityEquipment::setItemInOffHandDropChance)
            ;

            initEquipment(
                    equipmentSection,
                    "helmet",
                    livingPropertyList,
                    EntityEquipment::setHelmet,
                    EntityEquipment::setHelmetDropChance)
            ;

            initEquipment(
                    equipmentSection,
                    "chestplate",
                    livingPropertyList,
                    EntityEquipment::setChestplate,
                    EntityEquipment::setChestplateDropChance)
            ;

            initEquipment(
                    equipmentSection,
                    "leggings",
                    livingPropertyList,
                    EntityEquipment::setLeggings,
                    EntityEquipment::setLeggingsDropChance)
            ;

            initEquipment(
                    equipmentSection,
                    "boots",
                    livingPropertyList,
                    EntityEquipment::setBoots,
                    EntityEquipment::setBootsDropChance)
            ;
        }

        // Creeper
        final ConfigurationSection creeperSection = section.getConfigurationSection("creeper");
        if (creeperSection != null) {
            final EntityPropertyList<Creeper> creeperPropertyList = builder.getOrRegisterModifiers(Creeper.class);

            final boolean charged = creeperSection.getBoolean("charged", false);

            if (charged) {
                creeperPropertyList.add(creeper -> creeper.setPowered(true));
            }
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

    private static void initEquipment(
            ConfigurationSection equipmentSection,
            String path,
            EntityPropertyList<LivingEntity> livingPropertyList,
            EquipmentFunction equipmentMethod,
            BiConsumer<EntityEquipment, Float> dropMethod
    ) {
        final ConfigurationSection itemSection = equipmentSection.getConfigurationSection(path);
        if (itemSection != null) {
            final ItemBuilder itemBuilder = ItemBuilder.deserialize(itemSection);
            final ItemStack itemStack = itemBuilder.create(null);
            if (itemStack != null) {
                livingPropertyList.add(livingEntity -> {
                    final EntityEquipment equipment = livingEntity.getEquipment();
                    if (equipment != null) {
                        equipmentMethod.setEquipment(equipment, itemStack, true);
                        dropMethod.accept(equipment, 0.0f);
                    }
                });
            }
        }
    }

    @FunctionalInterface
    private interface EquipmentFunction {

        void setEquipment(EntityEquipment equipment, ItemStack itemStack, boolean silent);

    }

}
