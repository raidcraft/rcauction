package de.raidcraft.auction.api.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.auction.api.raidcraftevents.RE_AuctionCreate;
import de.raidcraft.auction.api.raidcraftevents.RE_AuctionStart;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerBid;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerDirectBuy;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerPickupItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * @author Dragonfire
 */
public class AuctionTrigger extends Trigger {

    public AuctionTrigger() {

        super("auction", "bid", "create", "start", "direct_buy", "pickup");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bid(RE_PlayerBid event) {
        Player player = Bukkit.getPlayer(event.getBid().getBidder());
        if(player == null) {
            return;
        }
        informListeners("bid", player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void create(RE_AuctionCreate event) {
        Player player = Bukkit.getPlayer(event.getAuction().getOwner());
        if(player == null) {
            return;
        }
        informListeners("create", player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void start(RE_AuctionStart event) {
        informListeners("start", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void direcBuy(RE_PlayerDirectBuy event) {
        informListeners("direct_buy", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickUp(RE_PlayerPickupItem event) {
        informListeners("pickup", event.getPlayer());
    }
}
