package de.raidcraft.auction.api.raidcraftevents;

import de.raidcraft.auction.model.TPlattform;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_PlayerOpenPlattform extends Event implements Cancellable {

    @Setter
    @Getter
    private TPlattform plattform;
    @Setter
    @Getter
    private Player player;
    @Setter
    @Getter
    boolean cancelled = false;

    public RE_PlayerOpenPlattform(TPlattform plattform, Player player) {

        this.plattform = plattform;
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
