package de.raidcraft.auction.model;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.ItemSelectorListener;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.MoneySelectorListener;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.api.pluginaction.RC_PluginAction;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionCreate;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class StartAuctionProcess {

    private AuctionPlugin plugin;
    private Player player;
    private String plattform;
    private int slot = -1;
    private double start_bid = -1;
    private double direct_buy = -1;
    private int durationInDays = -1;

    public StartAuctionProcess(AuctionPlugin plugin, Player player, String plattform) {

        this.plugin = plugin;
        this.player = player;
        this.plattform = plattform;
    }

    public void selectItem() {

        ChestUI.getInstance().selectItem(player, "Verkaufsitem auswählen", new ItemSelectorListener() {
            @Override
            public void cancel(Player player) {

                player.sendMessage("Auktionserstellung abgebrochen.");
            }

            @Override
            public void accept(Player player, int selectedSlot) {

                if (selectedSlot < 0) {
                    player.sendMessage("Kein gültiges Item ausgewählt");
                    return;
                }
                slot = selectedSlot;
                selectOptions();
            }
        });
    }

    public void selectOptions() {

        Menu menu = new Menu("Auktionsoptionen");
        menu.empty();
        menu.empty();
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                start_bid = 0;
                direct_buy = 0;
                selectStartBid();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.ORANGE, "Direktverkauf + Aktion")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                start_bid = 0;
                selectStartBid();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.RED, "nur Aktion")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                direct_buy = 0;
                selectDirectBuy();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.YELLOW, "nur Direktverkauf")));
        ChestUI.getInstance().openMenu(player, menu);
    }

    public void selectStartBid() {

        ChestUI.getInstance().openMoneySelection(player, "Startgebot", 0, new MoneySelectorListener() {
            @Override
            public void cancel(Player player) {

                player.sendMessage("Auktionserstellung abgebrochen");
            }

            @Override
            public void accept(Player player, double money) {

                start_bid = money;
                if (direct_buy >= 0) {
                    selectDirectBuy();
                } else {
                    selectDuration();
                }
            }
        });
    }

    public void selectDirectBuy() {

        ChestUI.getInstance().openMoneySelection(player, "SofortKaufWert", 0, new MoneySelectorListener() {
            @Override
            public void cancel(Player player) {

                player.sendMessage("Auktionserstellung abgebrochen");
            }

            @Override
            public void accept(Player player, double money) {

                direct_buy = money;
                selectDuration();
            }
        });
    }

    public void selectDuration() {

        Menu menu = new Menu("Auktionsoptionen");
        menu.empty();
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                durationInDays = 30;
                confirm();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.PURPLE, "30 Tage")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                durationInDays = 14;
                confirm();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.RED, "14 Tage")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                durationInDays = 7;
                confirm();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.ORANGE, "7 Tage")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                durationInDays = 3;
                confirm();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.YELLOW, "3 Tage")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                durationInDays = 1;
                confirm();
            }
        }.setItem(RC_Items.getGlassPane(DyeColor.LIME, "1 Tag")));
        ChestUI.getInstance().openMenu(player, menu);
    }

    public void confirm() {

        Menu menu = new Menu("Auktionsoptionen");
        menu.empty();
        menu.empty();
        menu.addMenuItem(new MenuItem().setItem(player.getInventory().getItem(slot)));

        if (start_bid >= 0) {
            MenuItem item_start_bid = new MenuItem();
            item_start_bid.setItem(AuctionPlugin.getPriceMaterial(start_bid), "Startgebot");
            RC_Items.setLore(item_start_bid.getItem(), "Startgebot: "
                    + RaidCraft.getEconomy().getFormattedAmount(start_bid));
            menu.addMenuItem(item_start_bid);
        } else {
            menu.empty();
        }

        if (direct_buy >= 0) {
            MenuItem item_direct_buy = new MenuItem();
            item_direct_buy.setItem(AuctionPlugin.getPriceMaterial(start_bid), "DirektKaufWert");
            RC_Items.setLore(item_direct_buy.getItem(), "DirektKaufWert: "
                    + RaidCraft.getEconomy().getFormattedAmount(direct_buy));
            menu.addMenuItem(item_direct_buy);
        } else {
            menu.empty();
        }

        MenuItemAPI duration = new MenuItem().setItem(RC_Items.getGlassPane(DyeColor.WHITE), "Aktionsdauer");
        duration.getItem().setAmount(durationInDays);
        RC_Items.setLore(duration.getItem(), "Aktionstage: " + durationInDays);
        menu.addMenuItem(duration);
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                create();
            }
        }.setItem(MenuItemAPI.getItemOk()));
        ChestUI.getInstance().openMenu(player, menu);
    }

    public void create() {

        player.closeInventory();
        PA_PlayerAuctionCreate action = new PA_PlayerAuctionCreate(player, plattform, slot,
                direct_buy, start_bid, durationInDays);
        RC_PluginAction.getInstance().fire(action);

    }


}
