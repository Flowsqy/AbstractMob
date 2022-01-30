package fr.flowsqy.abstractmob.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityPostLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Entity[] entities;

    public EntityPostLoadEvent(boolean isAsync, Entity[] entities) {
        super(isAsync);
        this.entities = entities;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Entity[] getEntities() {
        return entities;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
