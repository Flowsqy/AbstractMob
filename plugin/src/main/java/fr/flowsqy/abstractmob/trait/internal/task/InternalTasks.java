package fr.flowsqy.abstractmob.trait.internal.task;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitTaskManager;
import fr.flowsqy.abstractmob.trait.internal.task.loader.SpiderWebTaskLoader;

public class InternalTasks {

    public InternalTasks(AbstractMobPlugin plugin) {
        final TraitTaskManager taskManager = plugin.getTraitTaskManager();
        taskManager.registerTask(new SpiderWebTaskLoader(plugin));
    }

}
