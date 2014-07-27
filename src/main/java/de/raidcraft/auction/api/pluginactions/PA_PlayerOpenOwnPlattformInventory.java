package de.raidcraft.auction.api.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PA_PlayerOpenOwnPlattformInventory implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private String plattform;

    public PA_PlayerOpenOwnPlattformInventory(Player player, String plattform) {

        this.player = player;
        this.plattform = plattform;
    }
}
