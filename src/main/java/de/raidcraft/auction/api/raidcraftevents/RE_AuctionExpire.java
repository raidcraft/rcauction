package de.raidcraft.auction.api.raidcraftevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_AuctionExpire extends Event {

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}