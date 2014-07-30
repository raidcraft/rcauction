package de.raidcraft.auction.api.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.auction.api.raidcraftevents.RE_PlayerOpenPlattform;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Dragonfire
 */
public class PlattformTrigger  extends Trigger implements Listener {

    public PlattformTrigger() {

        super("plattform", "open");
    }

    @Information(value = "plattform.open",
            desc = "If the player open a plattform")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void ope(RE_PlayerOpenPlattform event) {
        informListeners("open", event.getPlayer());
    }
}
