package de.raidcraft.auction.pluginactions;

import de.raidcraft.api.pluginaction.PluginAction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PA_PlayerOpenPlattform implements PluginAction {

    @Getter
    private Player player;
    @Getter
    private String plattform;

    public PA_PlayerOpenPlattform(Player player, String plattform) {

        this.player = player;
        this.plattform = plattform;
    }
}
