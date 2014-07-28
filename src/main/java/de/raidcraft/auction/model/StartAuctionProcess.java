package de.raidcraft.auction.model;

import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.auction.AuctionPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class StartAuctionProcess {

    private AuctionPlugin plugin;
    private Player player;
    private String plattform;
    @Getter
    @Setter
    private int slot = -1;
    @Getter
    @Setter
    private int start_bid = -1;
    @Getter
    @Setter
    private int direct_buy = -1;

    public StartAuctionProcess(AuctionPlugin plugin, Player player, String plattform) {

        this.plugin = plugin;
        this.player = player;
        this.plattform = plattform;
    }

    public void itemSelected() {

        Menu menu = new Menu("Auktionsoptionen");
        menu.empty();
        menu.empty();
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                setStart_bid(0);
                setDirect_buy(0);
                selecteStartBid();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.ORANGE, "Direktverkauf + Aktion")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                setStart_bid(0);
                selecteStartBid();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.RED, "nur Aktion")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                setDirect_buy(0);
                selectDirectBuy();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.YELLOW, "nur Direktverkauf")));
        ChestUI.getInstance().openMenu(player, menu);
    }

    public void selecteStartBid() {

        ChestUI.getInstance().openMoneySelection(player, "Startgebot", 0.01);
    }

    public void selectDirectBuy() {

    }


}
