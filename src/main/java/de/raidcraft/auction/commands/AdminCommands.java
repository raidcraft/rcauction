package de.raidcraft.auction.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.api.pluginaction.RC_PluginAction;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.actions.PlayerAuctionStartAction;
import de.raidcraft.auction.actions.PlayerOpenPlattformAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                desc = "Create a new auction",
                min = 5,
                usage = "<plattform> <item_slot> <direct_buy> <start_bid> <duration_days> "
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Spielerkommando");
                return;
            }
            String plattform = context.getString(0);
            int slot = context.getInteger(1);
            double direct_buy = context.getDouble(2);
            double start_bid = context.getDouble(3);
            int duration_days = context.getInteger(4);

            PlayerAuctionStartAction action = new PlayerAuctionStartAction(
                    (Player) sender, plattform, slot, direct_buy, start_bid, duration_days);
            RC_PluginAction.getInstance().fire(action);

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
                min = 0,
                usage = "<plattformlist>"
        )
        @CommandPermissions("autcion.open")
        public void open(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Das ist ein Spieler Kommando!");
            }
            String player_plattform = (context.argsLength() == 0) ? "all" : context.getString(0);
            RC_PluginAction.getInstance().fire(new PlayerOpenPlattformAction((Player) sender, player_plattform));
        }
    }
}
