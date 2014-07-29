package de.raidcraft.auction.api.configactions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.auction.AuctionPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */

public class CA_PlayerAuctionStart implements Action<Player> {

    @Override
    public void accept(Player player, ConfigurationSection config) {

        String plattform = config.getString("plattform", null);
        RaidCraft.getComponent(AuctionPlugin.class).getAPI()
                .playerAuctionStart(player, plattform);
    }
}