package de.raidcraft.auction.api.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PA_PlayerAuctionDirectBuy implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private int auction;

    public PA_PlayerAuctionDirectBuy(Player player, int auction_id) {

        this.player = player;
        this.auction = auction_id;
    }
}
