package de.raidcraft.auction.configactions;

import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.pluginaction.RC_PluginAction;
import de.raidcraft.auction.pluginactions.PA_PlayerOpenPlattform;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
@ActionInformation(name = "AUCTION_OPEN_PLATTFORM")
public class CA_PlayerOpenPlattform extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String plattform = args.getString("plattform", null);
        Player player = conversation.getPlayer();
        RC_PluginAction.getInstance().fire(new PA_PlayerOpenPlattform(player, plattform));
    }
}