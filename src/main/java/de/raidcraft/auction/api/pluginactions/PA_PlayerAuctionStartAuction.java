package de.raidcraft.auction.api.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PA_PlayerAuctionStartAuction implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private String plattform;

    public PA_PlayerAuctionStartAuction(Player player, String plattform) {

        this.player = player;
        this.plattform = plattform;
    }
}
