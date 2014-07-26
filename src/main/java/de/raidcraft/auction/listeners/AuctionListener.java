package de.raidcraft.auction.listeners;

import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.api.pluginaction.PluginActionListener;
import de.raidcraft.api.pluginaction.RcPluginAction;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.pluginactions.PA_PlayerAuctionCreate;
import de.raidcraft.auction.pluginactions.PA_PlayerAuctionStartAuction;
import de.raidcraft.auction.pluginactions.PA_PlayerOpenPlattform;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Dragonfire
 */
public class AuctionListener implements PluginActionListener {

    private AuctionPlugin plugin;

    public AuctionListener(AuctionPlugin plugin) {

        this.plugin = plugin;
    }


    @RcPluginAction
    public void startAuction(PA_PlayerAuctionStartAuction action) {
        Player player = action.getPlayer();
        if (!player.hasPermission("auctions.start")) {
            player.sendMessage("Du kannst keine Auktion erstellen.");
            return;
        }
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            player.sendMessage("Plattform existiert nicht");
            return;
        }
        //int slot = ChestUI.get
    }

    @RcPluginAction
    public void createAuction(PA_PlayerAuctionCreate action) {

        Player player = action.getPlayer();
        if (!player.hasPermission("auctions.start")) {
            player.sendMessage("Du kannst keine Auktion einstellen.");
            return;
        }
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            player.sendMessage("Plattform existiert nicht");
            return;
        }
        ItemStack item = player.getInventory().getItem(action.getInventory_slot());
        if (item == null) {
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
        if (action.getStart_bid() > 0) {
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
    public void openPlattform(PA_PlayerOpenPlattform action) {

        Player player = action.getPlayer();
        if (!player.hasPermission("auctions.open")) {
            player.sendMessage("Du hast nicht das Recht diese Plattform zu öffnen!");
            return;
        }
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            player.sendMessage("Keine Plattformen zum handeln gefunden!");
            return;
        }

        List<TAuction> auctions = plugin.getActiveAuctions(plattform.getName());
        Menu menu = new Menu("Plattform: " + plattform.getName());
        int i = 0;
        for (TAuction auc : auctions) {
            ItemStack item = null;
            try {
                item = plugin.getItemForId(auc.getItem());
            } catch (StorageException e) {
                plugin.getLogger().warning("cannot load item " + auc.getStart_bid() + " for auction " + auc.getId());
            }
            menu.addMenuItem(new MenuItem().setItem(item));
            // TODO: find highest bid
            MenuItemAPI price = new MenuItem().setItem(getPriceMaterial(auc.getStart_bid()), "Preis");
            RC_Items.setLore(price.getItem(), "Startgebot: " + auc.getStart_bid(),
                    "Direktkauf: " + auc.getDirect_buy());
            menu.addMenuItem(price);

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm:ss");
            String endDate = format.format(auc.getAuction_end());

            // day item
            ItemStack days_normal = RC_Items.getGlassPane(DyeColor.WHITE);
            RC_Items.setDisplayName(days_normal, "Aktionstage");
            RC_Items.setLore(days_normal, "Ende: " + endDate);
            MenuItemAPI days = new MenuItemInteractive(null, days_normal,
                    getDateDiff(now, auc.getAuction_end(), TimeUnit.DAYS), 99);
            menu.addMenuItem(days);

            // hour item
            ItemStack hours_normal = RC_Items.getGlassPane(DyeColor.WHITE);
            RC_Items.setDisplayName(hours_normal, "Auktionsstunden");
            RC_Items.setLore(hours_normal, "Ende: " + endDate);
            MenuItemAPI hours = new MenuItemInteractive(null, hours_normal,
                    getDateDiff(now, auc.getAuction_end(), TimeUnit.HOURS) % 24, 99);
            menu.addMenuItem(hours);

            if (i % 2 == 0) {
                menu.empty();
            }
            i++;
        }
        ChestUI.getInstance().openMenu(player, menu);
    }

    public static int getDateDiff(Date oldDate, Date newDate, TimeUnit timeUnit) {

        long diffInMillies = newDate.getTime() - oldDate.getTime();
        return (int) timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private Material getPriceMaterial(double money) {

        if (money > 9999) {
            return Material.DIAMOND;
        }
        if (money > 99) {
            return Material.GOLD_INGOT;
        }
        if (money > 0.99) {
            return Material.IRON_INGOT;
        }
        return Material.NETHER_BRICK_ITEM;
    }
}
