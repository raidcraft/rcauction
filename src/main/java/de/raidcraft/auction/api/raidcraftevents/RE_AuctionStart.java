package de.raidcraft.auction.api.raidcraftevents;

import de.raidcraft.auction.tables.TPlattform;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_AuctionStart extends Event implements Cancellable {

    @Setter
    @Getter
    boolean cancelled = false;
    @Setter
    @Getter
    private Player player;
    @Setter
    @Getter
    private TPlattform plattform;

    public RE_AuctionStart(Player player, TPlattform plattform) {

        this.player = player;
        this.plattform = plattform;
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
