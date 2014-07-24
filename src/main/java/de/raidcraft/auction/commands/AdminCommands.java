package de.raidcraft.auction.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.chestui.ChestUI;
import de.raidcraft.api.chestui.Menu;
import de.raidcraft.api.chestui.menuitems.MenuItem;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.auction.AuctionPlugin;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Sebastian
 */
public class AdminCommands {

    public AdminCommands(AuctionPlugin module) {

    }

    @Command(
            aliases = {"auctions", "shop"},
            desc = "Control Dragonguard settings"
    )
    @NestedCommand(NestedDragonGuardCommands.class)
    public void auctions(CommandContext context, CommandSender sender) throws CommandException {

    }

    public static class NestedDragonGuardCommands {

        private final AuctionPlugin plugin;
        private final TranslationProvider tr;

        public NestedDragonGuardCommands(AuctionPlugin module) {

            this.plugin = module;
            this.tr = plugin.getTranslationProvider();
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload config and database"
        )
        @CommandPermissions("auction.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            plugin.reload();
            tr.msg(sender, "cmd.reload", "Plugin was sucessfully reloaded!");
        }

        @Command(
                aliases = {"create", "new"},
                desc = "Create a new plattform",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                return;
            }
            Menu menu = new Menu("testMenu");
            Random r = new Random();
            int n = r.nextInt(250);
            for (int i = 0; i < n; i++) {
                menu.addMenuItem(new MenuItem(Material.APPLE, "item: " + i));
            }
            ChestUI.getInstance().openMenu((Player) sender, menu);

            //            int costLevel = 1;
            //            boolean mainStation = false;
            //            boolean emergencyTarget = false;
            //
            //            if (context.hasFlag('c')) {
            //                costLevel = context.getFlagInteger('c', 1);
            //            }
            //
            //            if (context.hasFlag('m')) {
            //                mainStation = true;
            //            }
            //
            //            if (context.hasFlag('e')) {
            //                emergencyTarget = true;
            //            }
            //
            ////            DragonStation station;
            ////            try {
            ////                station = stationManager.createNewStation(context.getString(0)
            ////                        , context.getJoinedStrings(1)
            ////                        , ((Player) sender).getLocation()
            ////                        , costLevel
            ////                        , mainStation
            ////                        , emergencyTarget);
            ////            } catch (UnknownStationException e) {
            ////                throw new CommandException(e.getMessage());
            ////            }
            ////
            ////            NPCManager.createDragonGuard(station);
            ////
            ////            // dynmap
            ////            DynmapManager.INST.addStationMarker(station);
            //
            //            tr.msg(sender, "cmd.station.create", "You have created a dragon station with the name: %s", station.getDisplayName());
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Remove a plattform",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("autcions.add")
        public void remove(CommandContext context, CommandSender sender) throws CommandException {

            //            DragonStation station;
            //            try {
            //                station = (DragonStation) stationManager.getStation(context.getString(0));
            //            } catch (UnknownStationException e) {
            //                throw new CommandException(e.getMessage());
            //            }
            //
            //            NPCManager.removeDragonGuard(station);
            //
            //            stationManager.deleteStation(station);
            //            DynmapManager.INST.removeMarker(station);
            //            RaidCraft.getComponent(DragonTravelPlusPlugin.class).reload();
            //
            //            tr.msg(sender, "cmd.station.delete", "You deleted the dragon station: %s", station.getDisplayName());
        }

        @Command(
                aliases = {"open", "auctions"},
                desc = "Open the auction plattforms",
                min = 1,
                usage = "<plattformlist>"
        )
        @CommandPermissions("autcion.open")
        public void open(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Das ist ein Spieler Kommando!");
            }
            List<String> platts = new ArrayList<>();
            platts.add("all");
            platts.add("secret");
            //            plugin.openPlattform((Player) sender, platts);
            ChestUI.getInstance().openMoneySelection((Player) sender, "money select", 0);
        }
    }
}
