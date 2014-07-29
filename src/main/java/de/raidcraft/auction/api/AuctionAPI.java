package de.raidcraft.auction.api;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Dragonfire
 */
public interface AuctionAPI {

    public void playerAuctionBid(UUID player, int auction, double bid);

    public void playerAuctionCreate(Player player, String plattform, int inventorySlot,
                                    double startBid, double directBuy, int durationInDays);

    public void playerAuctionDirectBuy(Player player, int auction);

    public void playerAuctionStart(Player player, String plattform);

    public void playerOpenOwnPlattformInventory(Player player, String plattform);

    public void playerOpenPlattform(Player player, String plattform);

    public int createPlattform(String name);
}
