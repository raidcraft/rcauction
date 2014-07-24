package de.raidcraft.auction.actions;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.api.AuctionException;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Dragonfire
 */
public class PlayerOpenPlattformsAction implements Action<Player> {

    private AuctionPlugin plugin;

    public PlayerOpenPlattformsAction(AuctionPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    @SneakyThrows
    public void accept(Player player) {

        if (!player.hasPermission("auctions.open")) {
            throw new AuctionException("Du hast nicht das Recht das Auktionshaus zu öffnen!");
        }
        List<String> plattforms = getConfig().getStringList("plattforms");
        if (plattforms.size() < 1) {
            throw new AuctionException("Keine Plattformen zum handeln gefunden!");
        }
        throw new AuctionException("Es geht!");
    }
}
