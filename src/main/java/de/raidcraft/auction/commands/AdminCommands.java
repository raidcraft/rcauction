package de.raidcraft.auction.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.auction.tables.TPlattform;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
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
                aliases = {"pnew", "pcreate"},
                desc = "Creates a new plattform",
                min = 1,
                usage = "<plattform_name> "
        )
        @CommandPermissions("auction.plattform.create")
        public void pnew(CommandContext context, CommandSender sender) throws CommandException {

            TPlattform plattform = plugin.getPlattform(context.getString(0));
            if (plattform != null) {
                sender.sendMessage("Plattform already exists");
            }
            int id = plugin.getAPI().createPlattform(context.getString(0));
            if(id < 0) {
                sender.sendMessage("Name schon vergeben");
                return;
            }
            sender.sendMessage("Plattform created with id: " + id);
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
                aliases = {"start", "begin"},
                desc = "Start a new auction",
                min = 1,
                usage = "<plattform>"
        )
        @CommandPermissions("auction.create")
        public void start(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Spielerkommando");
                return;
            }
            plugin.getAPI().playerAuctionStart((Player) sender, context.getString(0));
        }

        @Command(
                aliases = {"create", "new"},
                desc = "Create a new auction",
                min = 5,
                usage = "<plattform> <item_slot> <direct_buy> <start_bid> <duration_days> "
        )
        @CommandPermissions("auction.create")
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

            plugin.getAPI().playerAuctionCreate(
                    (Player) sender, plattform, slot,
                    start_bid, direct_buy, duration_days);

        }

        @Command(
                aliases = {"pick", "grab", "pickup"},
                desc = "Holt Aktionsitems ab",
                min = 1,
                usage = "<plattform>"
        )
        @CommandPermissions("auction.pickup")
        public void pickup(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Das ist ein Spieler Kommando!");
            }
            TPlattform plattform = plugin.getPlattform(context.getString(0));
            if (plattform == null) {
                sender.sendMessage("Plattform existiert nicht!");
                return;
            }
            plugin.getAPI().playerOpenOwnPlattformInventory((Player) sender, plattform.getName());
        }

        @Command(
                aliases = {"open", "auctions"},
                desc = "Open the auction plattforms",
                min = 0,
                usage = "<plattformlist>"
        )
        @CommandPermissions("auction.plattform.open")
        public void open(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Das ist ein Spieler Kommando!");
            }
            String player_plattform = (context.argsLength() == 0) ? "all" : context.getString(0);
            plugin.getAPI().playerOpenPlattform((Player) sender, player_plattform);
        }

        @Command(
                aliases = {"bid"},
                desc = "Bid on a auction",
                min = 2,
                usage = "<auction_id> <bid>"
        )
        @CommandPermissions("auction.bid")
        public void bid(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Das ist ein Spieler Kommando!");
            }
            plugin.getAPI().playerAuctionBid(((Player) sender).getUniqueId(),
                    context.getInteger(0), context.getDouble(1));
        }

        @Command(
                aliases = {"buy", "directbuy"},
                desc = "directly buy a auction",
                min = 1,
                usage = "<auction_id>"
        )
        @CommandPermissions("auction.directbuy")
        public void directbuy(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) {
                sender.sendMessage("Das ist ein Spieler Kommando!");
            }
            plugin.getAPI().playerAuctionDirectBuy(
                    (Player) sender, context.getInteger(0));
        }
    }
}
