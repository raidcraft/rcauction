package de.raidcraft.auction;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Sebastian
 */
public interface AuctionAPI {

    public void showPlattforms(Player player, List<String> player_plattforms);

    public void openPlattform(Player player, String player_plattform);

    public void startAuction(Player player, int inventory_slot);
}
