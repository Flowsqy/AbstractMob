package fr.flowsqy.abstractmob.trait;

import fr.flowsqy.abstractmob.trait.internal.listener.TraitListener;

import java.util.LinkedList;
import java.util.List;

public class TraitListenerManager {

    private final List<TraitListener> listeners;

    public TraitListenerManager() {
        this.listeners = new LinkedList<>();
    }

    /**
     * Add a {@link TraitListener} to the manager
     *
     * @param listener The {@link TraitListener} to add
     */
    public void add(TraitListener listener) {
        listeners.add(listener);
    }

    /**
     * Register every {@link TraitListener}
     */
    public void registerAll() {
        for (TraitListener traitListener : listeners) {
            traitListener.register();
        }
    }

    /**
     * Unregister every {@link TraitListener}
     */
    public void unregisterAll() {
        for (TraitListener traitListener : listeners) {
            traitListener.unregister();
        }
    }

    /**
     * Clear the manager
     */
    public void clear() {
        listeners.clear();
    }

}
