package de.raidcraft.auction;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
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
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;
import io.ebean.SqlRow;
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

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

        timer.stop();
        timer = new AuctionTimer(this);
    }

    public void setupActionApi() {
	    ActionAPI.register(this)
			    .requirement("auction.has", new AuctionRequirement(this))
			    .trigger(new AuctionTrigger())
			    .trigger(new PlattformTrigger())
			    .action("start", new CA_PlayerAuctionStart())
			    .action("openplattforminventory", new CA_PlayerOpenOwnPlattformInventory())
			    .action("openplattform", new CA_PlayerOpenPlattform());
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

        List<TPlattform> platts = getRcDatabase().find(TPlattform.class)
                .where()
                .eq("name", name).setMaxRows(1).findList();
        return (platts.size() > 0) ? platts.get(0) : null;
    }

    public List<TAuction> getActiveAuctions(String plattform) {

        Date now = new Date();
        return getRcDatabase().find(TAuction.class).fetch("plattform")
                .where()
                .in("plattform", getPlattform(plattform))
                .gt("auction_end", now).findList();
    }

    public int getAuctionCount(UUID player) {

        String sql = "SELECT COUNT(*) c FROM auction_auctions WHERE owner = :player ";
        io.ebean.SqlRow row = getRcDatabase().createSqlQuery(sql).setParameter("player", player).findOne();
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

        return getRcDatabase().find(TBid.class).setRawSql(rawSql).where()
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

        return getRcDatabase().find(TBid.class).setRawSql(rawSql).where()
                .eq("auction.plattform.name", plattform)
                .eq("bidder", player)
                .findList();
    }

    public TBid getHeighestBid(int auction_id) {

        return getRcDatabase().find(TBid.class)
                .where().eq("auction_id", auction_id).order()
                .desc("bid").setMaxRows(1).findOne();

    }

    public TAuction getAuction(int auction_id) {

        return getRcDatabase().find(TAuction.class).where().eq("id", auction_id).findOne();
    }


    public long getNextAuctionEnd() {

        String sql = "SELECT TIME_TO_SEC(TIMEDIFF(auction_end, NOW())) next"
                + " FROM auction_auctions "
                + "WHERE auction_end > NOW() ORDER by auction_END ASC LIMIT 1";
        SqlRow row = getRcDatabase().createSqlQuery(sql).findOne();
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
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TPlattform.class);
        tables.add(TAuction.class);
        tables.add(TBid.class);
        return tables;
    }

    private void setupDatabase() {

        try {
            getRcDatabase();
        } catch (PersistenceException e) {
            e.printStackTrace();
            warning("Installing database for " + getDescription().getName() + " due to first time usage");
        }
    }

    public AuctionAPI getAPI() {

        return exectutor;
    }

    public AuctionAPI getProvider() {

        return getAPI();
    }
}
