package de.raidcraft.auction.api;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Dragonfire
 */
public interface AuctionAPI {

    void playerAuctionBid(UUID player, int auction, double bid);

    void playerAuctionCreate(Player player, String plattform, int inventorySlot,
                             double startBid, double directBuy, int durationInDays);

    void playerAuctionDirectBuy(Player player, int auction);

    void playerAuctionStart(Player player, String plattform);

    void playerOpenOwnPlattformInventory(Player player, String plattform);

    void playerOpenPlattform(Player player, String plattform);

    int createPlattform(String name);
}
