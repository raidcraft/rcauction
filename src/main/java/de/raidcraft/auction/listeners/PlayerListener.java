package de.raidcraft.auction.listeners;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Dragonfire
 */
public class PlayerListener implements Listener {

    @Getter
    private Set<UUID> playerWithEndedAuction = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (!playerWithEndedAuction.contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.getPlayer().sendMessage("Du hast etwas im Auktionshaus ersteigert");
        playerWithEndedAuction.remove(event.getPlayer().getUniqueId());
    }


}
