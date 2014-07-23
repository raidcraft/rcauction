package de.raidcraft.auction;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.auction.model.TAuction;
import de.raidcraft.auction.model.TBid;
import de.raidcraft.auction.model.TPlattform;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Sebastian
 */
public class AuctionPlugin extends BasePlugin {

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> tables = new ArrayList<>();
        tables.add(TPlattform.class);
        tables.add(TAuction.class);
        tables.add(TBid.class);
        return tables;
    }

    @Override
    public void enable() {
       setupDatabase();

        TPlattform plattform = new TPlattform();
        plattform.setName("pt");

        TAuction auction = new TAuction();
        auction.setItem(1337);
        auction.setOwner(UUID.randomUUID());
        auction.setPlattform(plattform);

        getDatabase().save(plattform);
        getDatabase().save(auction);
    }

    @Override
    public void disable() {
        //TODO: implement
    }

    private void setupDatabase() {
        try {
            int rowcount = getDatabase().find(TPlattform.class).findRowCount();
            getLogger().warning("ich: " + rowcount);
        } catch (PersistenceException ex) {
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
}
