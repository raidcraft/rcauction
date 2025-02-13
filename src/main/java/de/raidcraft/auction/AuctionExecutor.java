package de.raidcraft.auction;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.MoneySelectorListener;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.auction.api.AuctionAPI;
import de.raidcraft.auction.api.raidcraftevents.*;
import de.raidcraft.auction.listeners.PickupListener;
import de.raidcraft.auction.tables.StartAuctionProcess;
import de.raidcraft.auction.tables.TAuction;
import de.raidcraft.auction.tables.TBid;
import de.raidcraft.auction.tables.TPlattform;
import de.raidcraft.util.InventoryUtils;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Dragonfire
 */
public class AuctionExecutor implements AuctionAPI {

    private AuctionPlugin plugin;

    public AuctionExecutor(AuctionPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public void playerAuctionCreate(Player player, String sPlattform, int inventorySlot,
                                    double startBid, double directBuy, int durationInDays) {

        TPlattform plattform = plugin.getPlattform(sPlattform);
        if (plattform == null) {
            player.sendMessage("Plattform existiert nicht");
            return;
        }
        ItemStack item = player.getInventory().getItem(inventorySlot);
        if (item == null) {
            player.sendMessage("Kein Item gefunden.");
            return;
        }
        player.getInventory().clear(inventorySlot);
        int item_id = plugin.storeItem(item);

        TAuction auction = new TAuction();
        auction.setPlattform(plattform);
        auction.setOwner(player.getUniqueId());
        auction.setItem(item_id);
        auction.setDirect_buy(directBuy);
        auction.setStart_bid(startBid);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, durationInDays);
        auction.setAuction_end(cal.getTime());


        RE_AuctionCreate event = new RE_AuctionCreate(auction);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        plugin.getRcDatabase().save(auction);
        player.sendMessage("Auktion erfolgreich erstellt");

        // auto bid that if nobody bids the creator get it
        TBid creatorBid = new TBid();
        creatorBid.setAuction(auction);
        creatorBid.setBid(-1);
        creatorBid.setBidder(player.getUniqueId());
        plugin.getRcDatabase().save(creatorBid);

        plugin.getTimer().start();
    }

    @Override
    public void playerOpenPlattform(Player player, String sPlattform) {

        TPlattform plattform = plugin.getPlattform(sPlattform);
        if (plattform == null) {
            player.sendMessage("Keine Plattformen zum handeln gefunden!");
            return;
        }

        List<TAuction> auctions = plugin.getActiveAuctions(plattform.getName());
	    if(auctions.size() <= 0){
		    player.sendMessage("Keine aktuellen Auktionen vorhanden.");
		    return;
	    }

        Menu menu = new Menu("Plattform: " + plattform.getName());
        int i = 0;
        for (final TAuction auc : auctions) {
            ItemStack item = null;
            try {
                item = plugin.getItemForId(auc.getItem());
            } catch (StorageException e) {
                plugin.warning("cannot load item " + auc.getStart_bid() + " for auction " + auc.getId());
            }
            menu.addMenuItem(new MenuItemAPI() {

                @Override
                public void trigger(Player player) {

                    selectAuction(player, auc);
                }
            }.setItem(item));
            MenuItemAPI price = new MenuItemAPI() {

                @Override
                public void trigger(Player player) {

                    selectAuction(player, auc);
                }
            }.setItem(AuctionPlugin.getPriceMaterial(plugin.getMaxPriceValue(auc)), "Preis");
            ItemUtils.setLore(price.getItem(), auc.isAuction()
                            ? "Mindestgebot: "
                            + RaidCraft.getEconomy().getFormattedAmount(plugin.getMinimumBid(auc))
                            : "keine Auktion",
                    auc.isDirectBuy()
                            ? "Direktkauf: " + RaidCraft.getEconomy().getFormattedAmount(auc.getDirect_buy())
                            : "kein Direktkauf");
            menu.addMenuItem(price);

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm:ss");
            String endDate = format.format(auc.getAuction_end());

            // day item
            ItemStack days_normal = ItemUtils.getGlassPane(DyeColor.WHITE);
            ItemUtils.setDisplayName(days_normal, "Aktionstage");
            ItemUtils.setLore(days_normal, "Ende: " + endDate);
            MenuItemAPI days = new MenuItemInteractive(days_normal, null,
                    AuctionPlugin.getDateDiff(now, auc.getAuction_end(), TimeUnit.DAYS), 99);
            menu.addMenuItem(days);

            // hour item
            ItemStack hours_normal = ItemUtils.getGlassPane(DyeColor.WHITE);
            ItemUtils.setDisplayName(hours_normal, "Auktionsstunden");
            ItemUtils.setLore(hours_normal, "Ende: " + endDate);
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

    @Override
    public int createPlattform(String name) {

        if (plugin.getPlattform(name) != null) {
            return -1;
        }
        TPlattform plattform = new TPlattform();
        plattform.setName(name);
        plugin.getRcDatabase().save(plattform);
        return plattform.getId();
    }

    @Override
    public void playerOpenOwnPlattformInventory(Player player, String sPlattform) {

        TPlattform plattform = plugin.getPlattform(sPlattform);
        if (plattform == null) {
            player.sendMessage("Plattform nicht vorhanden: " + sPlattform);
        }

        RE_PlayerOpenPlattform event = new RE_PlayerOpenPlattform(plattform, player);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        List<TBid> sucessBids = plugin.getEndedAuction(
                player.getUniqueId(), sPlattform);
        Inventory inv = Bukkit.createInventory(player,
                InventoryUtils.COLUMN_COUNT * InventoryUtils.MAX_ROWS,
                "Lager: " + sPlattform);
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

    @Override
    public void playerAuctionStart(Player player, String sPlattform) {

        TPlattform plattform = plugin.getPlattform(sPlattform);
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

    @Override
    public void playerAuctionBid(UUID player, int iAuction, double dBid) {

        TAuction auction = plugin.getAuction(iAuction);
        if (auction == null) {
            return;
        }
        if (!RaidCraft.getEconomy().hasEnough(player, dBid)) {
            Bukkit.getPlayer(player).sendMessage("Du hast nicht so viel Geld");
            return;
        }
        if (dBid <= auction.getStart_bid()) {
            Bukkit.getPlayer(player).sendMessage("Dein Gebot ist zu niedrig");
            return;
        }
        TBid heighestBid = plugin.getHeighestBid(iAuction);
        if (heighestBid != null && dBid <= heighestBid.getBid()) {
            Bukkit.getPlayer(player).sendMessage("Es gibt bereits ein höheres Gebot");
            return;
        }
        TBid bid = new TBid();
        bid.setBid(dBid);
        bid.setBidder(player);
        bid.setAuction(auction);
        RE_PlayerBid event = new RE_PlayerBid(bid);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        plugin.getRcDatabase().save(bid);
        Bukkit.getPlayer(player).sendMessage("Erfolgreich geboten");
    }

    @Override
    public void playerAuctionDirectBuy(Player player, int iAuction) {

        TAuction auction = plugin.getAuction(iAuction);
        if (auction == null) {
            return;
        }
        RE_PlayerDirectBuy event = new RE_PlayerDirectBuy(auction, player);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (!RaidCraft.getEconomy().hasEnough(player.getUniqueId(), auction.getStart_bid())) {
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

        RaidCraft.getEconomy().substract(player.getUniqueId(), auction.getDirect_buy(),
                BalanceSource.AUCTION, "Direktkauf");
        RaidCraft.getEconomy().add(auction.getOwner(), auction.getDirect_buy(),
                BalanceSource.AUCTION, "Direktkauf");
        plugin.getRcDatabase().delete(auction);
        InventoryUtils.addOrDropItems(player, item);
        player.closeInventory();
        player.sendMessage("Erfolgreich gekauft");
    }

    // TODO: move to own class?
    public void selectAuction(final Player player, TAuction auction) {

        Menu menu = new Menu("Auktionsoptionen");
        ItemStack item;

        try {
            item = plugin.getItemForId(auction.getItem());
        } catch (StorageException e) {
            e.printStackTrace();
            return;
        }
        menu.empty();
        menu.addMenuItem(new MenuItem().setItem(item));
        MenuItemAPI price = new MenuItem().setItem(
                AuctionPlugin.getPriceMaterial(plugin.getMaxPriceValue(auction)), "Preis");
        ItemUtils.setLore(price.getItem(), "Startgebot: "
                        + RaidCraft.getEconomy().getFormattedAmount(plugin.getMinimumBid(auction)),
                "Direktkauf: " + RaidCraft.getEconomy().getFormattedAmount(auction.getDirect_buy()));
        menu.addMenuItem(price);

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm:ss");
        String endDate = format.format(auction.getAuction_end());

        // day item
        ItemStack days_normal = ItemUtils.getGlassPane(DyeColor.WHITE);
        ItemUtils.setDisplayName(days_normal, "Aktionstage");
        ItemUtils.setLore(days_normal, "Ende: " + endDate);
        MenuItemAPI days = new MenuItemInteractive(days_normal, null,
                plugin.getDateDiff(now, auction.getAuction_end(), TimeUnit.DAYS), 99);
        menu.addMenuItem(days);

        // hour item
        ItemStack hours_normal = ItemUtils.getGlassPane(DyeColor.WHITE);
        ItemUtils.setDisplayName(hours_normal, "Auktionsstunden");
        ItemUtils.setLore(hours_normal, "Ende: " + endDate);
        MenuItemAPI hours = new MenuItemInteractive(hours_normal, null,
                plugin.getDateDiff(now, auction.getAuction_end(), TimeUnit.HOURS) % 24, 99);
        menu.addMenuItem(hours);

        menu.empty();
        if (auction.getStart_bid() >= 0) {
            MenuItemAPI bid = new MenuItemAPI() {
                @Override
                public void trigger(Player player) {

                    playerStartBid(player, auction);
                }
            }.setItem(ItemUtils.getGlassPane(DyeColor.RED), "Bieten");
            ItemUtils.setLore(bid.getItem(), "Mindestgebot: "
                    + RaidCraft.getEconomy().getFormattedAmount(plugin.getMinimumBid(auction)));
            menu.addMenuItem(bid);
        } else {
            menu.empty();
        }

        if (auction.getDirect_buy() >= 0) {
            MenuItemAPI direct = new MenuItemAPI() {
                @Override
                public void trigger(Player player) {

                    playerAuctionDirectBuy(player, auction.getId());
                }
            }.setItem(ItemUtils.getGlassPane(DyeColor.YELLOW), "Direktkauf");
            ItemUtils.setLore(direct.getItem(), "Preis: "
                    + RaidCraft.getEconomy().getFormattedAmount(auction.getDirect_buy()));
            menu.addMenuItem(direct);
        } else {
            menu.empty();
        }

        ChestUI.getInstance().openMenu(player, menu);
    }

    // TODO: move to own class?
    public void playerStartBid(final Player player, final TAuction auction) {

        double heighestBid = plugin.getMinimumBid(auction);
        ChestUI.getInstance().openMoneySelection(player, "Dein Gebot", heighestBid, new MoneySelectorListener() {

            @Override
            public void cancel(Player player) {

                player.sendMessage("Du hast nichts geboten");
            }

            @Override
            public void accept(Player player, double money) {

                playerAuctionBid(player.getUniqueId(), auction.getId(), money);
            }
        });
    }

}