package fr.flowsqy.abstractmob.trait.internal.listener;

import fr.flowsqy.abstractmob.AbstractMobPlugin;
import fr.flowsqy.abstractmob.trait.TraitListenerManager;

public class InternalListeners {

    public InternalListeners(AbstractMobPlugin plugin) {
        final TraitListenerManager traitListenerManager = plugin.getTraitListenerManager();
        new ArrowResistanceListener(plugin, traitListenerManager);
        new KnockUpListener(plugin, traitListenerManager);
        new LightningListener(plugin, traitListenerManager);
        new SunResistanceListener(plugin, traitListenerManager);
        new CancelTransformationListener(plugin, traitListenerManager);
    }

}
