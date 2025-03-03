package fr.flowsqy.abstractmob.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.key.CustomKeys;
import fr.flowsqy.abstractmob.trait.ChancesChecker;

public class EntityBuilderSerializer {

    // Utility class
    private EntityBuilderSerializer() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * Deserialize an EntityBuilder from a configuration section
     *
     * @param plugin  The plugin instance used to get custom keys, set metadata and
     *                launch custom tasks
     * @param section The section to deserialize
     * @param logger  The logger to use for warnings
     * @return An {@link EntityBuilder} described by the specified section
     */
    @Nullable
    public static EntityBuilder deserialize(@NotNull AbstractMobPlugin plugin, @NotNull ConfigurationSection section,
            @NotNull Logger logger) {
        final String entityTypeRaw = section.getString("type");
        if (entityTypeRaw == null) {
            logger.warning("No entity type specified");
            return null;
        }
        final NamespacedKey entityTypeKey = NamespacedKey.fromString(entityTypeRaw);
        if (entityTypeKey == null) {
            logger.warning("'" + entityTypeRaw + "' is not a valid ressource location");
            return null;
        }
        final EntityType entityType = Registry.ENTITY_TYPE.get(entityTypeKey);
        final Class<? extends Entity> entityClass;
        if (entityType == null || entityType.equals(EntityType.UNKNOWN)
                || (entityClass = entityType.getEntityClass()) == null) {
            logger.warning("'" + entityTypeKey.toString() + "' is not a valid entity type");
            return null;
        }
        final EntityBuilder builder = new EntityBuilder(
                entityClass,
                section.getInt("quantity", 1),
                section.getInt("radius", 0));

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
                                new FixedMetadataValue(plugin, ChancesChecker.classicToPlugin(lightningChances))));
            }

            final int spiderwebChances = clamp(baseSection.getInt("web-on-walk", 0), 100, 0);
            if (spiderwebChances != 0) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.WEB_ON_WALK.getKey(),
                                new FixedMetadataValue(plugin, ChancesChecker.classicToPlugin(spiderwebChances))));

            }

            final double knockUp = baseSection.getDouble("knock-up", 0);
            if (knockUp != 0) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.KNOCK_UP.getKey(),
                                new FixedMetadataValue(plugin, knockUp)));
            }

            final boolean projectileResistance = baseSection.getBoolean("projectile-resistance", false);
            if (projectileResistance) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.PROJECTILE_RESISTANCE.getKey(),
                                new FixedMetadataValue(plugin, true)));
            }

            final boolean sunResistance = baseSection.getBoolean("sun-resistance", false);
            if (sunResistance) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.SUN_RESISTANCE.getKey(),
                                new FixedMetadataValue(plugin, true)));
            }

            final boolean cancelTransformation = baseSection.getBoolean("cancel-transformation", false);
            if (cancelTransformation) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.CANCEL_TRANSFORMATION.getKey(),
                                new FixedMetadataValue(plugin, true)));
            }

            final boolean transferTraits = baseSection.getBoolean("transfer-traits", false);
            if (transferTraits) {
                entityPropertyList
                        .add(entity -> entity.setMetadata(
                                customKeys.TRANSFER_TRAITS.getKey(),
                                new FixedMetadataValue(plugin, true)));
            }

            final String name = baseSection.getString("name");
            if (name != null) {
                final String coloredName = ChatColor.translateAlternateColorCodes('&', name);
                entityPropertyList.add(
                        entity -> {
                            entity.setCustomName(coloredName);
                            entity.setCustomNameVisible(true);
                            // Save it with placeholders
                            entity.setMetadata(
                                    customKeys.CUSTOM_NAME.getKey(),
                                    new FixedMetadataValue(plugin, coloredName));
                        });
            }
        }

        // Living properties
        final ConfigurationSection livingSection = section.getConfigurationSection("living");
        if (livingSection != null) {
            final EntityPropertyList<LivingEntity> livingPropertyList = builder
                    .getOrRegisterModifiers(LivingEntity.class);

            final boolean keepWhenFarAway = livingSection.getBoolean("keep-when-far-away", false);
            livingPropertyList.add(living -> living.setRemoveWhenFarAway(!keepWhenFarAway));

            final boolean trackLife = livingSection.getBoolean("track-life", false);
            if (trackLife) {
                livingPropertyList.add(
                        living -> {
                            living.setMetadata(
                                    customKeys.TRACK_LIFE.getKey(),
                                    new FixedMetadataValue(plugin, true));
                            plugin.getInternalListeners().getLifeTrackerListener().refreshLife(living);
                        });
            }

            // Potions properties
            final ConfigurationSection potionEffectsSection = livingSection.getConfigurationSection("potion-effects");
            if (potionEffectsSection != null) {
                final List<PotionEffect> potionEffects = new LinkedList<>();
                for (final String sectionKey : potionEffectsSection.getKeys(false)) {
                    final ConfigurationSection potionEffectSection = potionEffectsSection
                            .getConfigurationSection(sectionKey);
                    if (potionEffectSection == null) {
                        continue;
                    }
                    // Type
                    final String rawType = potionEffectSection.getString("type");
                    if (rawType == null || rawType.isBlank()) {
                        logger.warning("Potion type can't be null");
                        continue;
                    }
                    final NamespacedKey effectKey = NamespacedKey.fromString(rawType);
                    if (effectKey == null) {
                        logger.warning("'" + rawType + "' is not a valid ressource location");
                        continue;
                    }
                    final PotionEffectType type = Registry.EFFECT.get(effectKey);
                    if (type == null) {
                        logger.warning("'" + effectKey.toString() + "' is not a valid potion type");
                        continue;
                    }
                    potionEffects.add(new PotionEffect(
                            type,
                            Integer.MAX_VALUE,
                            Math.min(1, potionEffectSection.getInt("amplifier")) - 1,
                            potionEffectSection.getBoolean("ambient"),
                            potionEffectSection.getBoolean("particles")));
                }
                if (!potionEffects.isEmpty()) {
                    livingPropertyList.add(living -> {
                        for (PotionEffect potionEffect : potionEffects) {
                            living.addPotionEffect(potionEffect);
                        }
                    });
                }
            }
        }

        // Attribute properties
        final ConfigurationSection attributesSection = section.getConfigurationSection("attribute");
        if (attributesSection != null) {
            final EntityPropertyList<Attributable> attributablePropertyList = builder
                    .getOrRegisterModifiers(Attributable.class);

            for (String attributeKey : attributesSection.getKeys(false)) {
                final NamespacedKey storedAttributeKey;
                try {
                    storedAttributeKey = new NamespacedKey(plugin, attributeKey);
                } catch (Exception e) {
                    logger.warning("'" + attributeKey + "' is not a valid ressource key");
                    continue;
                }
                final ConfigurationSection attributeSection = attributesSection.getConfigurationSection(attributeKey);
                if (attributeSection == null) {
                    continue;
                }
                final String rawAttribute = attributeSection.getString("name");
                if (rawAttribute == null) {
                    logger.warning("'" + attributeKey + "' should specify an attribute name");
                    continue;
                }
                final NamespacedKey attributeNamespacedKey = NamespacedKey.fromString(rawAttribute);
                if (attributeNamespacedKey == null) {
                    logger.warning("'" + rawAttribute + "' is not a valid ressource location");
                    continue;
                }
                final Attribute attribute = Registry.ATTRIBUTE.get(attributeNamespacedKey);
                if (attribute == null) {
                    logger.warning("'" + attributeNamespacedKey.toString() + "' is not a valid attribute name");
                    continue;
                }
                final double value = attributeSection.getDouble("value", 0d);
                if (value == 0) {
                    continue;
                }
                final EquipmentSlotGroup slotGroup = getEquipmentSlotGroup(attributeSection.getString("slot"));
                final AttributeModifier.Operation operation = getEnumConstant(AttributeModifier.Operation.class,
                        attributeSection.getString("operation"));
                attributablePropertyList.add(attributable -> {
                    final AttributeInstance instance = attributable.getAttribute(attribute);
                    if (instance == null) {
                        return;
                    }
                    instance.addModifier(new AttributeModifier(
                            storedAttributeKey,
                            value,
                            operation == null ? AttributeModifier.Operation.ADD_NUMBER : operation,
                            slotGroup == null ? EquipmentSlotGroup.ANY : slotGroup));

                    if (attribute == Attribute.MAX_HEALTH && attributable instanceof Damageable damageable) {
                        damageable.setHealth(instance.getValue());
                    }

                });
            }
        }

        // Equipment properties
        final ConfigurationSection equipmentSection = section.getConfigurationSection("equipment");
        if (equipmentSection != null) {
            final EntityPropertyList<LivingEntity> livingPropertyList = builder
                    .getOrRegisterModifiers(LivingEntity.class);

            initEquipment(
                    equipmentSection,
                    "main-hand",
                    livingPropertyList,
                    EntityEquipment::setItemInMainHand,
                    EntityEquipment::setItemInMainHandDropChance);

            initEquipment(
                    equipmentSection,
                    "off-hand",
                    livingPropertyList,
                    EntityEquipment::setItemInOffHand,
                    EntityEquipment::setItemInOffHandDropChance);

            initEquipment(
                    equipmentSection,
                    "helmet",
                    livingPropertyList,
                    EntityEquipment::setHelmet,
                    EntityEquipment::setHelmetDropChance);

            initEquipment(
                    equipmentSection,
                    "chestplate",
                    livingPropertyList,
                    EntityEquipment::setChestplate,
                    EntityEquipment::setChestplateDropChance);

            initEquipment(
                    equipmentSection,
                    "leggings",
                    livingPropertyList,
                    EntityEquipment::setLeggings,
                    EntityEquipment::setLeggingsDropChance);

            initEquipment(
                    equipmentSection,
                    "boots",
                    livingPropertyList,
                    EntityEquipment::setBoots,
                    EntityEquipment::setBootsDropChance);
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

    private static EquipmentSlotGroup getEquipmentSlotGroup(String value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case "any" -> EquipmentSlotGroup.ANY;
            case "armor" -> EquipmentSlotGroup.ARMOR;
            case "chest" -> EquipmentSlotGroup.CHEST;
            case "feet" -> EquipmentSlotGroup.FEET;
            case "hand" -> EquipmentSlotGroup.HAND;
            case "head" -> EquipmentSlotGroup.HEAD;
            case "legs" -> EquipmentSlotGroup.LEGS;
            case "mainhand" -> EquipmentSlotGroup.MAINHAND;
            case "offhand" -> EquipmentSlotGroup.OFFHAND;
            default -> null;
        };
    }

    @Nullable
    private static <T extends Enum<T>> T getEnumConstant(@NotNull Class<T> enumClass, @Nullable String value) {
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
            BiConsumer<EntityEquipment, Float> dropMethod) {
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
