package de.raidcraft.auction.api.requirements;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.auction.AuctionPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class AuctionRequirement implements Requirement<Player> {

    private AuctionPlugin plugin;

    public AuctionRequirement(AuctionPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean test(Player player, ConfigurationSection config) {

        int count = plugin.getAuctionCount(player.getUniqueId());
        int needed = config.isSet("count") ? 1 : config.getInt("count");
        return count >= needed;
    }


}
