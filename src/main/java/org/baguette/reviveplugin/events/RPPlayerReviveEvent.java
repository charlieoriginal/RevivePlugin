package org.baguette.reviveplugin.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RPPlayerReviveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private Player reviver;
    @Getter @Setter private boolean cancelled = false;

    public RPPlayerReviveEvent(Player reviver) {
        this.reviver = reviver;
    }

    /*
    Event Requirements
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
