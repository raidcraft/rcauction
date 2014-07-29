package de.raidcraft.auction;

import de.raidcraft.auction.listeners.PlayerListener;
import de.raidcraft.auction.model.TBid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Dragonfire
 */
public class AuctionTimer implements Runnable {

    private AuctionPlugin plugin;
    private PlayerListener listener;
    private Set<Integer> announcedAuctions = new HashSet<>();
    private int taskId = -1;

    public AuctionTimer(AuctionPlugin plugin) {

        this.listener = new PlayerListener();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this);
    }

    @Override
    public void run() {

        List<TBid> bids = plugin.getEndedAuction();
        for (TBid bid : bids) {
            if (announcedAuctions.contains(bid.getId())) {
                continue;
            }
            notifyPlayer(bid.getBidder());
        }
        // add announced bids
        announcedAuctions.clear();
        for (TBid bid : bids) {
            announcedAuctions.add(bid.getId());
        }
        start();
    }

    public synchronized void start() {

        long next = plugin.getNextAuctionEnd();
        if (next < 0) {
            taskId = -1;
            return;
        }
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, next);
    }

    public void notifyPlayer(UUID player_id) {

        Player player = Bukkit.getPlayer(player_id);
        if (player == null) {
            listener.getPlayerWithEndedAuction().add(player_id);
            return;
        }
        player.sendMessage("Ein Item liegt im Auktionshaus bereit");
    }


}
