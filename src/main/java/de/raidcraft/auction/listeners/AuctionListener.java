package de.raidcraft.auction.listeners;

import de.raidcraft.api.pluginaction.PluginActionListener;
import de.raidcraft.api.pluginaction.RcPluginAction;
import de.raidcraft.auction.actions.PlayerBidAction;
import org.bukkit.Bukkit;

/**
 * @author Dragonfire
 */
public class AuctionListener implements PluginActionListener {

    @RcPluginAction
    public void fireEvent(PlayerBidAction action) {

        Bukkit.broadcastMessage("bid action");
    }
}
