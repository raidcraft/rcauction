package de.raidcraft.auction.actions;

import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PlayerOpenOwnPlattformInventoryAction {

    @Getter
    private Player player;
    @Getter
    private String plattform;

    public PlayerOpenOwnPlattformInventoryAction(Player player, String plattform) {

        this.player = player;
        this.plattform = plattform;
    }
}
