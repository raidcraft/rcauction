package de.raidcraft.auction.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Entity
@Table(name = "auction_auctions")
public class TAuction {

    @Getter
    @Setter
    @Id
    private int id;
    @Getter
    @Setter
    @NotNull
    private UUID owner;
    @Getter
    @Setter
    @NotNull
    private int item;
    @Getter
    @Setter
    @NotNull
    @ManyToOne
    private TPlattform plattform;
    @Getter
    @Setter
    private double direct_buy;
    @Getter
    @Setter
    private Date auction_end;
    @Getter
    @Setter
    private double start_bid;
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "auction")
    private List<TBid> all_bids = new ArrayList<TBid>();


    public void Auction() {

    }

    public TAuction() {

    }
}
