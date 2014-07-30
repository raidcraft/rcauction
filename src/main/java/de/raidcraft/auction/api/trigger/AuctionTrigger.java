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
import org.bukkit.event.Listener;

/**
 * @author Dragonfire
 */
public class AuctionTrigger extends Trigger implements Listener {

    public AuctionTrigger() {

        super("auction", "bid", "create", "start", "direct_buy", "pickup");
    }

    @Information(value = "auction.bid",
            desc = "If the player bid")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bid(RE_PlayerBid event) {

        Player player = Bukkit.getPlayer(event.getBid().getBidder());
        if (player == null) {
            return;
        }
        informListeners("bid", player);
    }

    @Information(value = "auction.create",
            desc = "If the player create a auction")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void create(RE_AuctionCreate event) {

        Player player = Bukkit.getPlayer(event.getAuction().getOwner());
        if (player == null) {
            return;
        }
        informListeners("create", player);
    }

    @Information(value = "auction.start",
            desc = "If the player start the auction create process")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void start(RE_AuctionStart event) {

        informListeners("start", event.getPlayer());
    }

    @Information(value = "auction.direct_buy",
            desc = "If a player buy a item rom an auction")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void direcBuy(RE_PlayerDirectBuy event) {

        informListeners("direct_buy", event.getPlayer());
    }

    @Information(value = "auction.pickup",
            desc = "If a player pick up a item from a auction trader, "
                    + "can be the heighest bidder or the author, because nobody bid")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickUp(RE_PlayerPickupItem event) {

        informListeners("pickup", event.getPlayer());
    }
}
