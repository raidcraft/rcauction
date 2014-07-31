package de.raidcraft.auction.listeners;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerPickupItem;
import de.raidcraft.auction.model.TBid;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * @author Dragonfire
 */
public class PickupListener implements Listener {

    private Player player;
    private Inventory storage;
    private List<TBid> bids;
    private AuctionPlugin plugin;

    public PickupListener(Player player, Inventory storage, List<TBid> bids, AuctionPlugin plugin) {

        this.player = player;
        this.storage = storage;
        this.bids = bids;
        this.plugin = plugin;
    }

    @EventHandler
    public void interact(InventoryClickEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player) || ((Player) holder) != player) {
            return;
        }
        event.setCancelled(true);
        // only react on pickup events
        if (event.getAction() != InventoryAction.PICKUP_ALL) {
            return;
        }
        int slot = event.getSlot();
        // if not top inventory selected, abort
        if (event.getRawSlot() >= storage.getSize()) {
            return;
        }
        int newslot = player.getInventory().firstEmpty();
        if (newslot < 0) {
            player.sendMessage("Dein Inventar ist voll.");
            return;
        }
        double bidAmount = bids.get(slot).getBid();
        if(!RaidCraft.getEconomy().hasEnough(player.getUniqueId(), bidAmount)) {
            player.sendMessage("Du hast zu wenig Geld, gebraucht wird: "
                    + RaidCraft.getEconomy().getFormattedAmount(bidAmount));
            return;
        }
        RE_PlayerPickupItem pickupevent =
                new RE_PlayerPickupItem((Player) holder, storage.getItem(slot));
        RaidCraft.callEvent(pickupevent);
        if (pickupevent.isCancelled()) {
            return;
        }
        player.getInventory().setItem(newslot, storage.getItem(slot));
        storage.clear(slot);
        RaidCraft.getEconomy().substract(player.getUniqueId(), bidAmount,
                BalanceSource.AUCTION, "Auktion");
        RaidCraft.getEconomy().add(bids.get(slot).getAuction().getOwner(), bidAmount,
                BalanceSource.AUCTION, "Auktion");
        plugin.getDatabase().delete(bids.get(slot).getAuction());
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Player) || ((Player) holder) != player) {
            return;
        }
        HandlerList.unregisterAll(this);
    }
}