package de.raidcraft.auction.listeners;

import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.PickupListener;
import de.raidcraft.api.pluginaction.PluginActionListener;
import de.raidcraft.api.pluginaction.RcPluginAction;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionStart;
import de.raidcraft.auction.model.StartAuctionProcess;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class StartAuctionListener  implements PluginActionListener {

    private AuctionPlugin plugin;

    public StartAuctionListener(AuctionPlugin plugin) {

        this.plugin = plugin;
    }


    @RcPluginAction
    public void startAuction(PA_PlayerAuctionStart action) {

        Player player = action.getPlayer();
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            player.sendMessage("Plattform existiert nicht");
            return;
        }
        final StartAuctionProcess process = new StartAuctionProcess(plugin, player, plattform.getName());
        ChestUI.getInstance().selectItem(player, "Verkaufsitem auswählen", new PickupListener() {
            @Override
            public void cancel(Player player) {
                player.sendMessage("Auktionserstellung abgebrochen.");
            }

            @Override
            public void accept(Player player, int itemSlot) {
                process.setSlot(itemSlot);
                process.itemSelected();
            }
        });
    }


}