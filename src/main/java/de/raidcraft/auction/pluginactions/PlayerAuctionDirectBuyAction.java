package de.raidcraft.auction.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PlayerAuctionDirectBuyAction implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private int auction_id;

    public PlayerAuctionDirectBuyAction(Player player, int auction_id) {

        this.player = player;
        this.auction_id = auction_id;
    }
}
