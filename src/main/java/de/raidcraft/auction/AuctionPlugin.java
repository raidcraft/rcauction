package de.raidcraft.auction;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.MoneySelectorListener;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.api.pluginaction.RC_PluginAction;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionBid;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionDirectBuy;
import de.raidcraft.auction.commands.AdminCommands;
import de.raidcraft.auction.listeners.AuctionListener;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TBid;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.PersistenceException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Sebastian
 */
public class AuctionPlugin extends BasePlugin implements AuctionAPI {

    public Map<String, TPlattform> plattforms = new HashMap<>();
    private ItemStorage itemStore;

    @Override
    public void enable() {

        itemStore = new ItemStorage(getName());
        setupDatabase();
        registerCommands(AdminCommands.class);
        RC_PluginAction.getInstance().registerAction(new AuctionListener(this));
    }

    @Override
    public void showPlattforms(Player player, List<String> s_plattforms) {

        List<TPlattform> player_plattforms = new ArrayList<>();
        for (String s_plattform : s_plattforms) {
            if (this.plattforms.containsKey(s_plattform)) {
                player_plattforms.add(this.plattforms.get(s_plattform));
            }
        }
        Menu menu = new Menu("Choose your Plattform");
        for (TPlattform platt : player_plattforms) {
            MenuItem item = new MenuItem();
            ItemMeta meta = item.getItem().getItemMeta();
            meta.setDisplayName(platt.getName());
            item.getItem().setItemMeta(meta);
            menu.addMenuItem(item);
        }
        ;
        ChestUI.getInstance().openMenu(player, menu);
    }

    public static Material getPriceMaterial(double money) {

        if (money > 9999) {
            return Material.DIAMOND;
        }
        if (money > 99) {
            return Material.GOLD_INGOT;
        }
        if (money > 0.99) {
            return Material.IRON_INGOT;
        }
        if (money == 0) {
            return Material.RAW_FISH;
        }
        return Material.NETHER_BRICK_ITEM;
    }

    public TPlattform getPlattform(String name) {

        List<TPlattform> platts = getDatabase().find(TPlattform.class)
                .where()
                .eq("name", name).setMaxRows(1).findList();
        return (platts.size() > 0) ? platts.get(0) : null;
    }

    public List<TAuction> getActiveAuctions(String plattform) {

        Date now = new Date();
        return getDatabase().find(TAuction.class).fetch("plattform")
                .where()
                .in("plattform", getPlattform(plattform))
                .gt("auction_end", now).findList();
    }

    public List<TAuction> getEndedAuctions(String plattform) {

        Date now = new Date();
        return getDatabase().find(TAuction.class).fetch("plattform")
                .where()
                .in("plattform", getPlattform(plattform))
                .lt("auction_end", now).findList();
    }


    // SELECT * FROM auction_bids a WHERE bid = (SELECT MAX(bid) FROM auction_bids b WHERE a.auction_id = b.auction_id)
    public List<TBid> getEndedAuction(UUID player, String plattform) {

        String max_bids
                = "SELECT b.id, b.auction_id, b.bid, b.bidder, a.owner, a.plattform_id, "
                + "p.name, a.item, a.direct_buy, a.auction_end, a.start_bid FROM auction_bids b "
                + "LEFT JOIN auction_auctions a ON a.id = b.auction_id "
                + "LEFT JOIN auction_plattforms p ON a.plattform_id = p.id "
                + "WHERE bid = (SELECT MAX(b2.bid) FROM auction_bids b2 WHERE b.auction_id = b2.auction_id) "
                + "AND a.auction_end < NOW()";
        RawSql rawSql = RawSqlBuilder
                // let ebean parse the SQL so that it can
                // add expressions to the WHERE and HAVING
                // clauses
                .parse(max_bids)
                        // map resultSet columns to bean properties
                .columnMapping("b.id", "id")
                .columnMapping("b.auction_id", "auction.id")
                .columnMapping("b.bid", "bid")
                .columnMapping("b.bidder", "bidder")
                .columnMapping("a.owner", "auction.owner")
                .columnMapping("a.plattform_id", "auction.plattform.id")
                .columnMapping("p.name", "auction.plattform.name")
                .columnMapping("a.item", "auction.item")
                .columnMapping("a.direct_buy", "auction.direct_buy")
                .columnMapping("a.auction_end", "auction.auction_end")
                .columnMapping("a.start_bid", "auction.start_bid")
                .create();

        return getDatabase().find(TBid.class).setRawSql(rawSql).where()
                .eq("auction.plattform.name", plattform)
                .eq("bidder", player)
                .findList();
    }

    public TBid getHeighestBid(int auction_id) {

        return getDatabase().find(TBid.class)
                .where().eq("auction_id", auction_id).order()
                .desc("bid").setMaxRows(1).findUnique();

    }

    public TAuction getAuction(int auction_id) {

        return getDatabase().find(TAuction.class).where().eq("id", auction_id).findUnique();
    }


    public int storeItem(ItemStack item) {

        return this.itemStore.storeObject(item);
    }

    public ItemStack getItemForId(int item_id) throws StorageException {

        return this.itemStore.getObject(item_id);
    }

    public int createPlattform(String name) {

        TPlattform plattform = new TPlattform();
        plattform.setName(name);
        getDatabase().save(plattform);
        return plattform.getId();
    }

    public void selectAuction(final Player player, TAuction auction) {

        Menu menu = new Menu("Auktionsoptionen");
        ItemStack item;

        try {
            item = getItemForId(auction.getItem());
        } catch (StorageException e) {
            e.printStackTrace();
            return;
        }
        menu.empty();
        menu.addMenuItem(new MenuItem().setItem(item));
        MenuItemAPI price = new MenuItem().setItem(AuctionPlugin.getPriceMaterial(auction.getStart_bid()), "Preis");
        RC_Items.setLore(price.getItem(), "Startgebot: " + RaidCraft.getEconomy().getFormattedAmount(auction.getStart_bid()),
                "Direktkauf: " + RaidCraft.getEconomy().getFormattedAmount(auction.getDirect_buy()));
        menu.addMenuItem(price);

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm:ss");
        String endDate = format.format(auction.getAuction_end());

        // day item
        ItemStack days_normal = RC_Items.getGlassPane(DyeColor.WHITE);
        RC_Items.setDisplayName(days_normal, "Aktionstage");
        RC_Items.setLore(days_normal, "Ende: " + endDate);
        MenuItemAPI days = new MenuItemInteractive(days_normal, null,
                getDateDiff(now, auction.getAuction_end(), TimeUnit.DAYS), 99);
        menu.addMenuItem(days);

        // hour item
        ItemStack hours_normal = RC_Items.getGlassPane(DyeColor.WHITE);
        RC_Items.setDisplayName(hours_normal, "Auktionsstunden");
        RC_Items.setLore(hours_normal, "Ende: " + endDate);
        MenuItemAPI hours = new MenuItemInteractive(hours_normal, null,
                getDateDiff(now, auction.getAuction_end(), TimeUnit.HOURS) % 24, 99);
        menu.addMenuItem(hours);

        menu.empty();
        if (auction.getStart_bid() >= 0) {
            menu.addMenuItem(new MenuItemAPI() {
                @Override
                public void trigger(Player player) {

                    playerStartBid(player, auction);
                }
            }.setItem(RC_Items.getGlassPane(DyeColor.RED), "Bieten"));
        } else {
            menu.empty();
        }

        if (auction.getDirect_buy() >= 0) {
            MenuItemAPI direct = new MenuItemAPI() {
                @Override
                public void trigger(Player player) {

                    RC_PluginAction.getInstance().fire(
                            new PA_PlayerAuctionDirectBuy(player, auction.getId()));
                }
            }.setItem(RC_Items.getGlassPane(DyeColor.YELLOW), "Direktkauf");
            RC_Items.setLore(direct.getItem(), "Preis: "
                    + RaidCraft.getEconomy().getFormattedAmount(auction.getDirect_buy()));
            menu.addMenuItem(direct);
        } else {
            menu.empty();
        }

        ChestUI.getInstance().openMenu(player, menu);
    }

    public void playerStartBid(final Player player, final TAuction auction) {

        TBid hBid = getHeighestBid(auction.getId());
        double heighestBid = (hBid == null) ? auction.getStart_bid() : hBid.getBid();
        ChestUI.getInstance().openMoneySelection(player, "Dein Gebot", heighestBid, new MoneySelectorListener() {

            @Override
            public void cancel(Player player) {

                player.sendMessage("Du hast nichts geboten");
            }

            @Override
            public void accept(Player player, double money) {

                RC_PluginAction.getInstance().fire(
                        new PA_PlayerAuctionBid(player.getUniqueId(), auction.getId(), money));
            }
        });
    }


    public static int getDateDiff(Date oldDate, Date newDate, TimeUnit timeUnit) {

        long diffInMillies = newDate.getTime() - oldDate.getTime();
        return (int) timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disable() {
        //TODO: implement
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TPlattform.class);
        tables.add(TAuction.class);
        tables.add(TBid.class);
        return tables;
    }

    private void setupDatabase() {

        try {
            for (TPlattform plattform : getDatabase().find(TPlattform.class).findList()) {
                plattforms.put(plattform.getName(), plattform);
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
}
