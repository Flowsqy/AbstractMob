# AbstractMob

A plugin Bukkit which offer an API to create entity with custom traits like spawning lightning at entity death with
ease. It also unifies the configuration format to allow simple yaml configuration for servers owners.

## Configuration

```yaml
<key>:
  type: <type>
  quantity: <quantity>
  base:
    lightning-on-death: <lightning-chances>
    web-on-walk: <web-chances>
    knockback-up: <knockback-up>
    projectile-resistance: <projectile-resistance>
  attribute:
    <attribute-1>:
      value: <attribute-value>
      operation: <attribute-operation>
      slot: <attribute-slot>
    <attribute-2>:
      ...
  equipment:
    <equipment-slot>: <equiment-item>
  creeper:
    charged: <creeper-charged>


# Where:
#
# <key> is the root key
# <type> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html] (Except UNKNOWN) :
#   the entity type. This is the only required tag. It can not be null
# <quantity> [integer] : The number of entity to spawn
#
# Base:
# <lightning-chances> [integer] : (Must be between 0 and 100): The chances to spawn a lightning when entity died
# <web-chances> [integer] : (Must be between 0 and 100): The chances to spawn a cobweb when the entity change location
# <knockback-up> [double] : The up value when a player is knocked by the entity. (In m/tick [1sec = 20 ticks])
# <projectile-resistance> [boolean] : Whether the entity avoid damage from projectile
#
# Attribute:
# <attribute-n> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html] :
#   The attribute to modify. It will be skipped if the value is null, if the attribute is invalid or if the attribute
#   isn't compatible with the specified entity type. You can have as many attribute as you want
# <attribute-value> [double] : The modifier value (REQUIRED only for this attribute, not for the global entity)
# <attribute-operation> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/AttributeModifier.Operation.html] :
#   The operation to apply to the attribute. ADD_NUMBER by default
# <attribute-slot> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/EquipmentSlot.html] :
#   The slot in which the modifier is active. If not present, the modifier is effective permanently (default)
#
# Equipment:
# <equipment-slot> ['main-hand', 'off-hand', 'helmet', 'chestplate', 'leggings' or 'boots'] :
#   The equipment slot of the Entity to set
# <equipment-item> [https://github.com/Flowsqy/AbstractMenu/template.yml] :
#   The item to set on the specific slot. Will be ignored if the entity can't support the slot of the item
#
# Creeper:
# <creeper-charged> [boolean] : true to spawn a charged creeper
```

## Developers

How to include the API with Maven:

```xml

<project>
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.Flowsqy</groupId>
            <artifactId>AbstractMob</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

If you have the configuration :

```yaml
custom-zombie:
  type: ZOMBIE
  quantity: 2
```

You can deserialize it like that :

```java
package fr.flowsqy.customraids;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.entity.EntityBuilder;
import fr.flowsqy.abstractmob.entity.EntityBuilderSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomRaidsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final YamlConfiguration configuration; // You need to link it to the configuration
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("AbstractMob");
        if (plugin instanceof AbstractMobPlugin abstractMobPlugin) {
            final EntityBuilder customEntity = EntityBuilderSerializer.deserialize(
                    abstractMobPlugin,
                    configuration.getConfigurationSection("custom-zombie")
            );
            // Do whatever you want with the custom zombie if it exists
            if (customEntity != null) {
                // You can spawn it :
                customEntity.spawn(abstractMobPlugin, new Location(0, 100, 0));
            }
        }
    }
}
```

## Building

Just clone the repository and do `mvn clean install` or `mvn clean package`