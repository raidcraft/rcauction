package de.raidcraft.auction.listeners;

import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.pluginaction.PluginActionListener;
import de.raidcraft.api.pluginaction.RcPluginAction;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.actions.PlayerAuctionStartAction;
import de.raidcraft.auction.actions.PlayerOpenPlattformAction;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Dragonfire
 */
public class AuctionListener implements PluginActionListener {

    private AuctionPlugin plugin;

    public AuctionListener(AuctionPlugin plugin) {

        this.plugin = plugin;
    }


    @RcPluginAction
    public void startAuction(PlayerAuctionStartAction action) {
        Player player = action.getPlayer();
        if (!player.hasPermission("auctions.start")) {
            player.sendMessage("Du kannst keine Auktion starten.");
            return;
        }
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if(plattform == null) {
            player.sendMessage("Plattform existiert nicht");
            return;
        }
        ItemStack item = player.getInventory().getItem(action.getInventory_slot());
        if(item == null) {
            player.sendMessage("Kein Item gefunden.");
            return;
        }
        player.getInventory().clear(action.getInventory_slot());
        int item_id = plugin.storeItem(item);

        TAuction auction = new TAuction();
        auction.setPlattform(plattform);
        auction.setOwner(player.getUniqueId());
        auction.setItem(item_id);
        auction.setDirect_buy(action.getDirect_buy());
        auction.setStart_bid(action.getStart_bid());
        if(action.getStart_bid() > 0) {
            // TODO: time functions
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, action.getDuration_days());
            auction.setAuction_end(cal.getTime());
        }

        plugin.getDatabase().save(auction);
        // TODO: event
        player.sendMessage("Auktion erfolgreich erstellt");
    }

    @RcPluginAction
    public void openPlattform(PlayerOpenPlattformAction action) {

        Player player = action.getPlayer();
        if (!player.hasPermission("auctions.open")) {
            player.sendMessage("Du hast nicht das Recht diese Plattform zu öffnen!");
            return;
        }
        if (action.getPlattform() == null) {
            player.sendMessage("Keine Plattformen zum handeln gefunden!");
            return;
        }
        String plattform = action.getPlattform();
        Menu menu = new Menu("Plattform: " + plattform);
        for (int i = 0; i < 800; i++) {
            try {
                menu.addMenuItem(new MenuItem(new ItemStack(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ChestUI.getInstance().openMenu(player, menu);
        Bukkit.broadcastMessage("bid action");
    }
}
