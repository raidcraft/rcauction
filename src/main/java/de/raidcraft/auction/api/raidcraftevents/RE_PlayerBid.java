package de.raidcraft.auction.api.raidcraftevents;

import de.raidcraft.auction.tables.TBid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_PlayerBid extends Event implements Cancellable {

    @Setter
    @Getter
    private TBid bid;

    @Setter
    @Getter
    boolean cancelled = false;

    public RE_PlayerBid(TBid bid) {

        this.bid = bid;
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