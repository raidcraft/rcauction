package de.raidcraft.auction.api.raidcraftevents;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class RE_PlayerPickupItem extends Event implements Cancellable {

    @Setter
    @Getter
    boolean cancelled = false;
    @Setter
    @Getter
    private Player player;
    @Setter
    @Getter
    private ItemStack item;

    public RE_PlayerPickupItem(Player player, ItemStack item) {

        this.player = player;
        this.item = item;
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
