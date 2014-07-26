package de.raidcraft.auction.api.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PA_PlayerAuctionCreate implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private int inventory_slot;
    @Getter
    private double start_bid; // negative = deactived
    @Getter
    private double direct_buy; // negative = deactived
    @Getter
    private int duration_days;
    @Getter
    private String plattform;


    public PA_PlayerAuctionCreate(Player player, String plattform, int inventory_slot,
                                  double direct_buy, double start_bid, int duration_days) {

        this.player = player;
        this.plattform = plattform;
        this.inventory_slot = inventory_slot;
        this.start_bid = start_bid;
        this.direct_buy = direct_buy;
        this.duration_days = duration_days;
    }
}
