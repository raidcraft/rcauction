package de.raidcraft.auction.actions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PlayerOpenPlattformAction implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private String plattform;

    public PlayerOpenPlattformAction(Player player, String plattform) {

        this.player = player;
        this.plattform = plattform;
    }
}
