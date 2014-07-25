package de.raidcraft.auction.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;

import java.util.UUID;

/**
 * @author Dragonfire
 */
public class PlayerAuctionBidAction implements PluginAction {

    @Getter
    private UUID player;
    @Getter
    private int auction;
    @Getter
    private double bid;

    public PlayerAuctionBidAction(UUID player, int auction, double bid) {

        this.player = player;
        this.auction = auction;
        this.bid = bid;
    }
}
