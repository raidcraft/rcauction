package de.raidcraft.auction.api.requirements;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.model.TAuction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

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
        // TODO: optimize count query
        List<TAuction> auctions = plugin.getAuction(player.getUniqueId());
        return auctions != null && auctions.size() > 0;
    }


}
