package de.raidcraft.auction.api.raidcraftevents;

import de.raidcraft.auction.tables.TAuction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
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

    @Setter
    @Getter
    private Player player;

    public RE_PlayerDirectBuy(TAuction auction, Player player) {

        this.auction = auction;
        this.player = player;
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