package de.raidcraft.auction.api.configactions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.auction.AuctionPlugin;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */

@ActionInformation(name = "AUCTION_START")
public class CA_PlayerAuctionStart extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String plattform = args.getString("plattform", null);
        Player player = conversation.getPlayer();
        RaidCraft.getComponent(AuctionPlugin.class).getAPI()
                .playerAuctionStart(player, plattform);
    }
}