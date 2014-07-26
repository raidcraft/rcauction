package de.raidcraft.auction;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.SqlRow;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.pluginaction.RC_PluginAction;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.auction.commands.AdminCommands;
import de.raidcraft.auction.listeners.AuctionListener;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TBid;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<TAuction> getActiveAuctions(String plattform) {

        List<TAuction> list = new ArrayList<>();
        TPlattform t_plattform = this.plattforms.get(plattform);
        if (plattform == null) {
            return list;
        }
        // TODO: fix it
        return getDatabase().find(TAuction.class).fetch("plattform").
                where().eq("plattform.name", t_plattform.getName()).
                where().not(Expr.gt("NOW()", "auction_end")).findList();
    }

    // SELECT * FROM auction_bids a WHERE bid = (SELECT MAX(bid) FROM auction_bids b WHERE a.auction_id = b.auction_id)
    public List<Integer> getItemsForGrab(UUID player, TPlattform plattform) {

        String sql = "select order_id, sum(order_qty*unit_price) as total_amount from o_order_detail  where order_qty > :minQty  group by order_id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("minQty", 1)
                .findList();

        // just getting the first row of the list
        SqlRow sqlRow = sqlRows.get(0);

        Integer id = sqlRow.getInteger("order_id");
        Double amount = sqlRow.getDouble("total_amount");
        getDatabase().find(TBid.class).fetch("auction").where().lt("auction_end", "NOW()");
        return null;
    }

    public UUID getHeighestBidder(int auction_id) {

        return getDatabase().find(TBid.class)
                .where().eq("auction", auction_id)
                .order("bid DESC").setMaxRows(1).findList().get(0).getBidder();

    }

    public TPlattform getPlattform(String plattform_name) {

        return plattforms.get(plattform_name);
    }

    public int storeItem(ItemStack item) {

        return this.itemStore.storeObject(item);
    }

    public ItemStack getItemForId(int item_id) throws StorageException {

        return this.itemStore.getObject(item_id);
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
