package de.raidcraft.auction.api.raidcraftevents;

import de.raidcraft.auction.model.TAuction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_PlayerDirectBuy extends Event implements Cancellable {

    @Setter
    @Getter
    private TAuction auction;

    @Setter
    @Getter
    boolean cancelled = false;

    public RE_PlayerDirectBuy(TAuction auction) {

        this.auction = auction;
    }
    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}