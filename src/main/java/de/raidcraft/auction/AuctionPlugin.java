package de.raidcraft.auction;

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

    public void openPlattform(Player player, String player_plattform) {

        Menu menu = new Menu(player_plattform + " Seite 1");
        ChestUI.getInstance().openMenu(player, menu);
    }

    public void startAuction(Player player, int inventory_slot) {

        ItemStack item = player.getInventory().getItem(inventory_slot);


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
