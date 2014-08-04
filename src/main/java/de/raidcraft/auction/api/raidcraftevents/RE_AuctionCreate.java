package de.raidcraft.auction.api.raidcraftevents;

import de.raidcraft.auction.tables.TAuction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_AuctionCreate extends Event implements Cancellable {

    @Setter
    @Getter
    boolean cancelled = false;
    @Setter
    @Getter
    private TAuction auction;

    public RE_AuctionCreate(TAuction auction) {

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
