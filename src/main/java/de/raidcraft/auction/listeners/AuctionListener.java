package de.raidcraft.auction.listeners;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.inventory.RC_Inventory;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.api.pluginaction.PluginActionListener;
import de.raidcraft.api.pluginaction.RcPluginAction;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionBid;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionCreate;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionDirectBuy;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionStart;
import de.raidcraft.auction.api.pluginactions.PA_PlayerOpenOwnPlattformInventory;
import de.raidcraft.auction.api.pluginactions.PA_PlayerOpenPlattform;
import de.raidcraft.auction.api.raidcraftevents.RE_AuctionCreate;
import de.raidcraft.auction.api.raidcraftevents.RE_AuctionStart;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerBid;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerDirectBuy;
import de.raidcraft.auction.model.StartAuctionProcess;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TBid;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    public void createAuction(PA_PlayerAuctionCreate action) {

        Player player = action.getPlayer();
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

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, action.getDuration_days());
        auction.setAuction_end(cal.getTime());


        RE_AuctionCreate event = new RE_AuctionCreate(auction);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        plugin.getDatabase().save(auction);
        player.sendMessage("Auktion erfolgreich erstellt");
    }

    @RcPluginAction
    public void openPlattform(PA_PlayerOpenPlattform action) {

        Player player = action.getPlayer();
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            player.sendMessage("Keine Plattformen zum handeln gefunden!");
            return;
        }

        List<TAuction> auctions = plugin.getActiveAuctions(plattform.getName());
        Menu menu = new Menu("Plattform: " + plattform.getName());
        int i = 0;
        for (final TAuction auc : auctions) {
            ItemStack item = null;
            try {
                item = plugin.getItemForId(auc.getItem());
            } catch (StorageException e) {
                plugin.getLogger().warning("cannot load item " + auc.getStart_bid() + " for auction " + auc.getId());
            }
            menu.addMenuItem(new MenuItemAPI() {

                @Override
                public void trigger(Player player) {

                    plugin.selectAuction(player, auc);
                }
            }.setItem(item));
            MenuItemAPI price = new MenuItemAPI() {

                @Override
                public void trigger(Player player) {

                    plugin.selectAuction(player, auc);
                }
            }.setItem(AuctionPlugin.getPriceMaterial(auc.getStart_bid()), "Preis");
            RC_Items.setLore(price.getItem(), "Mindesgebot: "
                    + RaidCraft.getEconomy().getFormattedAmount(plugin.getMinimumBid(auc)),
                    "Direktkauf: " + RaidCraft.getEconomy().getFormattedAmount(auc.getDirect_buy()));
            menu.addMenuItem(price);

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm:ss");
            String endDate = format.format(auc.getAuction_end());

            // day item
            ItemStack days_normal = RC_Items.getGlassPane(DyeColor.WHITE);
            RC_Items.setDisplayName(days_normal, "Aktionstage");
            RC_Items.setLore(days_normal, "Ende: " + endDate);
            MenuItemAPI days = new MenuItemInteractive(days_normal, null,
                    AuctionPlugin.getDateDiff(now, auc.getAuction_end(), TimeUnit.DAYS), 99);
            menu.addMenuItem(days);

            // hour item
            ItemStack hours_normal = RC_Items.getGlassPane(DyeColor.WHITE);
            RC_Items.setDisplayName(hours_normal, "Auktionsstunden");
            RC_Items.setLore(hours_normal, "Ende: " + endDate);
            MenuItemAPI hours = new MenuItemInteractive(hours_normal, null,
                    AuctionPlugin.getDateDiff(now, auc.getAuction_end(), TimeUnit.HOURS) % 24, 99);
            menu.addMenuItem(hours);

            if (i % 2 == 0) {
                menu.empty();
            }
            i++;
        }
        ChestUI.getInstance().openMenu(player, menu);
    }

    @RcPluginAction
    public void openPlattformInventory(PA_PlayerOpenOwnPlattformInventory action) {

        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            action.getPlayer().sendMessage("Plattform nicht vorhanden: " + action.getPlattform());
        }
        Player player = action.getPlayer();
        List<TBid> sucessBids = plugin.getEndedAuction(
                player.getUniqueId(), action.getPlattform());
        Inventory inv = Bukkit.createInventory(player,
                RC_Inventory.COLUMN_COUNT * RC_Inventory.MAX_ROWS,
                "Lager: " + action.getPlattform());
        int slot = 0;
        for (TBid bid : sucessBids) {
            try {
                inv.setItem(slot, plugin.getItemForId(bid.getAuction().getItem()));
            } catch (StorageException e) {
                e.printStackTrace();
            }
            slot++;
            if (slot >= inv.getSize()) {
                player.sendMessage("Du hast sehr viele Items ersteigert");
                break;
            }
        }
        Bukkit.getPluginManager().registerEvents(new PickupListener(player, inv, sucessBids, plugin), plugin);
        player.openInventory(inv);
    }

    @RcPluginAction
    public void startAuction(PA_PlayerAuctionStart action) {

        Player player = action.getPlayer();
        TPlattform plattform = plugin.getPlattform(action.getPlattform());
        if (plattform == null) {
            player.sendMessage("Plattform existiert nicht");
            return;
        }

        RE_AuctionStart event = new RE_AuctionStart(player, plattform);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        final StartAuctionProcess process =
                new StartAuctionProcess(plugin, player, plattform.getName());
        process.selectItem();
    }

    @RcPluginAction
    public void playerBid(PA_PlayerAuctionBid action) {

        TAuction auction = plugin.getAuction(action.getAuction());
        if (auction == null) {
            return;
        }
        // TODO: convert to UUID
        if (!RaidCraft.getEconomy().hasEnough(
                Bukkit.getPlayer(action.getPlayer()).getName(), action.getBid())) {
            Bukkit.getPlayer(action.getPlayer()).sendMessage("Du hast nicht so viel Geld");
            return;
        }
        if (action.getBid() <= auction.getStart_bid()) {
            Bukkit.getPlayer(action.getPlayer()).sendMessage("Dein Gebot ist zu niedrieg");
            return;
        }
        TBid heighestBid = plugin.getHeighestBid(action.getAuction());
        if (heighestBid != null && action.getBid() >= heighestBid.getBid()) {
            Bukkit.getPlayer(action.getPlayer()).sendMessage("Es gibt bereits ein höheres Gebot");
            return;
        }
        TBid bid = new TBid();
        bid.setBid(action.getBid());
        bid.setBidder(action.getPlayer());
        bid.setAuction(auction);
        RE_PlayerBid event = new RE_PlayerBid(bid);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        plugin.getDatabase().save(bid);
        Bukkit.getPlayer(action.getPlayer()).sendMessage("Erfolgreich geboten");
    }

    @RcPluginAction
    public void playerDirectBuy(PA_PlayerAuctionDirectBuy action) {

        TAuction auction = plugin.getAuction(action.getAuction());
        if (auction == null) {
            return;
        }
        RE_PlayerDirectBuy event = new RE_PlayerDirectBuy(auction);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Player player = action.getPlayer();

        if (!RaidCraft.getEconomy().hasEnough(player.getName(), auction.getStart_bid())) {
            player.sendMessage("Du hast nicht genügend Geld");
            return;
        }

        ItemStack item = null;
        try {
            item = plugin.getItemForId(auction.getItem());
        } catch (StorageException e) {
            e.printStackTrace();
        }
        if (item == null) {
            player.sendMessage("Konnte Auktionsitems nicht laden: " + auction.getId());
            return;
        }
        // TODO: delete item in storage?
        plugin.getDatabase().delete(auction);
        RaidCraft.getEconomy().substract(player.getName(), auction.getStart_bid(),
                BalanceSource.AUCTION, "Direktkauf");
        HashMap<Integer, ItemStack> dropItems = player.getInventory().addItem(item);
        for (ItemStack stack : dropItems.values()) {
            player.getWorld().dropItem(player.getLocation(), stack);
        }
        player.sendMessage("Erfolgreich gekauft");
    }
}
