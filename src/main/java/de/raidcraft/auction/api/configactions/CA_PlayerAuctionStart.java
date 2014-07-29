package de.raidcraft.auction.api.configactions;

import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.pluginaction.RC_PluginAction;
import de.raidcraft.auction.api.pluginactions.PA_PlayerAuctionStart;
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
        RC_PluginAction.getInstance().fire(new PA_PlayerAuctionStart(player, plattform));
    }
}