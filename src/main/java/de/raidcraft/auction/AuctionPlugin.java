package de.raidcraft.auction;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.MenuItem;
import de.raidcraft.auction.actions.PlayerOpenPlattformsAction;
import de.raidcraft.auction.commands.AdminCommands;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TBid;
import de.raidcraft.auction.model.TPlattform;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian
 */
public class AuctionPlugin extends BasePlugin {

    public Map<String, TPlattform> plattforms = new HashMap<>();

    @Override
    public void enable() {

        setupDatabase();
        registerActions();
        registerCommands(AdminCommands.class);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                getLogger().warning("actiontime");
                MemoryConfiguration config = new MemoryConfiguration();
                config.set("plattforms", new String[]{"all", "secrets"});
                try {
                    ActionFactory.getInstance().create("auction.open", config);
                } catch (ActionException e) {
                    e.printStackTrace();
                }
            }
        }, 20 * 10);
    }

    public void openPlattform(Player player, List<String> s_plattforms) {
        List<TPlattform> player_plattforms = new ArrayList<>();
        for(String s_plattform : s_plattforms) {
            if(this.plattforms.containsKey(s_plattform)) {
                player_plattforms.add(this.plattforms.get(s_plattform));
            }
        }
        Menu menu = new Menu("Choose your Plattform");
        for(TPlattform platt : player_plattforms) {
            MenuItem item = new MenuItem();
            ItemMeta meta = item.getItem().getItemMeta();
            meta.setDisplayName(platt.getName());
            item.getItem().setItemMeta(meta);
            menu.addMenuItem(item);
        };
        ChestUI.getInstance().openMenu(player, menu);
    }

    @Override
    public void disable() {
        //TODO: implement
    }

    private void registerActions() {

        ActionFactory.getInstance().registerGlobalAction("auction.open", new PlayerOpenPlattformsAction(this));
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
        } catch (PersistenceException ex) {
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
}
