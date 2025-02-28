# AbstractMob

A Bukkit plugin which offer an API to create entity with custom traits like 'summon lightning at entity death' with
ease. It also unifies the configuration format to allow simple yaml configuration for servers owners.

## Configuration

```yaml
<key>:
  type: <type>
  quantity: <quantity>
  radius: <radius>
  base:
    lightning-on-death: <lightning-chances>
    web-on-walk: <web-chances>
    knock-up: <knock-up>
    projectile-resistance: <projectile-resistance>
    sun-resistance: <sun-resistance>
    cancel-transformation: <cancel-transformation>
    transfer-traits: <transfer-traits>
    name: <name>
  living:
    keep-when-far-away: <living-keep>
    track-life: <track-life>
    potion-effects:
      <potion-effect-n>:
        type: <potion-type>
        amplifier: <potion-amplifier>
        ambient: <potion-ambient>
        particles: <potion-particles>
  attribute:
    <attribute-1>:
      name: <attribute-name>
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
# <type> [https://minecraft.wiki/w/Java_Edition_data_values#Entities]:
#   the entity type. This is the only required tag. It can not be null
# <quantity> [integer] : The number of entity to spawn
# <radius> [integer] : The radius of the circle where entity should spawn
#
# Base:
# <lightning-chances> [integer] : (Must be between 0 and 100): The chances to spawn a lightning when entity died
# <web-chances> [integer] : (Must be between 0 and 100): The chances to spawn a cobweb when the entity change location
# <knock-up> [double] : The up value when a player is knocked by the entity. (In m/tick [1sec = 20 ticks])
# <projectile-resistance> [boolean] : Whether the entity avoid damage from projectile
# <sun-resistance> [boolean] : Whether the entity avoid burning from the sun
# <cancel-transformation> [boolean] : Whether entity transformation should be blocked (e.g. Zombie in drowned)
# <transfer-traits> [boolean] : Whether the entity should transfer its traits when it transforms
# <name> [String] : The custom name of the entity. Support colors
#
# Living:
# <living-keep> [boolean] : Whether the entity is kept when the chunk is unloaded
# <track-life> [boolean] : Whether the life of the entity should be tracked.
#   When it sets to true, it adds '%health%', '%max-health%' and '%health-percentage%' placeholders in the custom name.
#   If it's unset or set to false, these placeholders will not be replaced
#
# Potion:
# <potion-effect-n> [String] : The key of the potion effect section.
#   It can be whatever you want, it just needs to be unique.
# <potion-type> [https://minecraft.wiki/w/Java_Edition_data_values#Effects] :
#   The type of the potion effect. It's the only required property to apply the effect.
# <potion-amplifier> [integer] : The potion 'level'. It must be greater than 0. 1 by default.
# <potion-ambient> [boolean] : Whether the potion effect show massive particles. false by default.
# <potion-particles> [boolean] : Whether the potion effect show particles. false by default.
#
# Attribute:
# <attribute-n> [String] :
#   The attribute key which will store the attribute. It should respect the ressource location format specified by minecraft
# <attribute-name> [https://minecraft.wiki/w/Attribute#Attributes] :
#   The attribute to modify. It will be skipped if the value is null, if the attribute is invalid or if the attribute
#   isn't compatible with the specified entity type. You can have as many attribute as you want
# <attribute-value> [double] : The modifier value (REQUIRED only for this attribute, not for the global entity)
# <attribute-operation> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/AttributeModifier.Operation.html] :
#   The operation to apply to the attribute. ADD_NUMBER by default
# <attribute-slot> ['any', 'armor', 'chest', 'feet', 'hand', 'head', 'legs', 'mainhand', 'offhand'] :
#   The slot group in which the modifier is active. 'any' by default
#
# Equipment:
# <equipment-slot> ['main-hand', 'off-hand', 'helmet', 'chestplate', 'leggings' or 'boots'] :
#   The equipment slot of the Entity to set
# <equipment-item> [https://github.com/Flowsqy/AbstractMenu (item section)] :
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

If you have this configuration :

```yaml
custom-zombie:
  type: zombie
  quantity: 2
```

You can deserialize it like that :

```java
package fr.flowsqy.abstractmobexample;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.entity.EntityBuilder;
import fr.flowsqy.abstractmob.entity.EntityBuilderSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AbstractMobExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final YamlConfiguration configuration; // You need to link it to the configuration
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("AbstractMob");
        if (plugin instanceof AbstractMobPlugin abstractMobPlugin) {
            // Deserialize the entity
            final EntityBuilder customEntity = EntityBuilderSerializer.deserialize(
                    abstractMobPlugin,
                    configuration.getConfigurationSection("custom-zombie"),
                    getLogger()
            );
            // Do whatever you want with the custom zombie if it exists
            if (customEntity != null) {
                // For example, you can spawn it :
                customEntity.spawn(abstractMobPlugin, new Location(Bukkit.getWorld("world"), 0, 100, 0));
            }
        }
    }
}
```

And it will spawn two zombie at 0 100 0 in the world 'world'

## Building

Just clone the repository and do `mvn clean install` or `mvn clean package`. The jar is in the _target_ directory.
