package de.raidcraft.auction;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.auction.api.AuctionAPI;
import de.raidcraft.auction.api.configactions.CA_PlayerAuctionStart;
import de.raidcraft.auction.api.configactions.CA_PlayerOpenOwnPlattformInventory;
import de.raidcraft.auction.api.configactions.CA_PlayerOpenPlattform;
import de.raidcraft.auction.api.requirements.AuctionRequirement;
import de.raidcraft.auction.api.trigger.AuctionTrigger;
import de.raidcraft.auction.api.trigger.PlattformTrigger;
import de.raidcraft.auction.commands.AdminCommands;
import de.raidcraft.auction.tables.TAuction;
import de.raidcraft.auction.tables.TBid;
import de.raidcraft.auction.tables.TPlattform;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Dragonfire
 */
public class AuctionPlugin extends BasePlugin {

    public Map<String, TPlattform> plattforms = new HashMap<>();
    private ItemStorage itemStore;
    private AuctionAPI exectutor;
    @Getter
    private AuctionTimer timer;

    @Override
    public void enable() {

        itemStore = new ItemStorage(getName());
        setupDatabase();
        registerCommands(AdminCommands.class);
        exectutor = new AuctionExecutor(this);
        setupActionApi();

        timer = new AuctionTimer(this);
    }

    public void setupActionApi() {

        try {
            ActionFactory.getInstance().registerAction(
                    this, "start", new CA_PlayerAuctionStart());
            ActionFactory.getInstance().registerAction(
                    this, "openplattforminventory", new CA_PlayerOpenOwnPlattformInventory());
            ActionFactory.getInstance().registerAction(
                    this, "openplattform", new CA_PlayerOpenPlattform());

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequirementFactory.getInstance().registerRequirement(this, "auction.has", new AuctionRequirement(this));

        TriggerManager.getInstance().registerTrigger(this, new AuctionTrigger());
        TriggerManager.getInstance().registerTrigger(this, new PlattformTrigger());
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

    public int getAuctionCount(UUID player) {

        String sql = "SELECT COUNT(*) c FROM auction_auctions WHERE owner = :player ";
        SqlRow row = getDatabase().createSqlQuery(sql).setParameter("player", player).findUnique();
        return (row == null) ? -1 : row.getInteger("c").intValue();
    }

    public List<TBid> getEndedAuction() {

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
                .findList();
    }

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


    public long getNextAuctionEnd() {

        String sql = "SELECT TIME_TO_SEC(TIMEDIFF(auction_end, NOW())) next"
                + " FROM auction_auctions "
                + "WHERE auction_end > NOW() ORDER by auction_END ASC LIMIT 1";
        SqlRow row = getDatabase().createSqlQuery(sql).findUnique();
        return (row == null) ? -1 : row.getLong("next").longValue();
    }

    public int storeItem(ItemStack item) {

        return this.itemStore.storeObject(item);
    }

    public ItemStack getItemForId(int item_id) throws StorageException {

        return this.itemStore.getObject(item_id);
    }


    public double getMinimumBid(TAuction auction) {

        TBid hBid = getHeighestBid(auction.getId());
        if (hBid == null || hBid.getBid() < auction.getStart_bid()) {
            return auction.getStart_bid();
        }
        return hBid.getBid();
    }

    public double getMaxPriceValue(TAuction auction) {
        return Math.max(getMinimumBid(auction), auction.getDirect_buy());
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
            getDatabase();
        } catch (PersistenceException e) {
            e.printStackTrace();
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

    public AuctionAPI getAPI() {

        return exectutor;
    }

    public AuctionAPI getProvider() {

        return getAPI();
    }
}
