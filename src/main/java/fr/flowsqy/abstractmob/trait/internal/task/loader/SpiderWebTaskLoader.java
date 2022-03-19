package fr.flowsqy.abstractmob.trait.internal.task.loader;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.BukkitTaskLoader;
import fr.flowsqy.abstractmob.trait.internal.task.SpiderWebTask;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;

public class SpiderWebTaskLoader implements BukkitTaskLoader {

    private final AbstractMobPlugin plugin;

    public SpiderWebTaskLoader(AbstractMobPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BukkitTask start(Entity entity) {
        if (entity == null) {
            return null;
        }
        final List<MetadataValue> values = entity.getMetadata(plugin.getCustomKeys().WEB_ON_WALK.getKey());
        if (values.isEmpty()) {
            return null;
        }
        final Optional<MetadataValue> pluginValue = values.stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .findAny();
        if (pluginValue.isEmpty()) {
            return null;
        }
        return new SpiderWebTask(entity, plugin.getChancesChecker(), pluginValue.get().asInt()).runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public String getIdentifier() {
        return plugin.getCustomKeys().WEB_ON_WALK.getKey();
    }
}
