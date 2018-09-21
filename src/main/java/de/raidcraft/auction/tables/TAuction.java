package de.raidcraft.auction.tables;

import io.ebean.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "auction_auctions")
public class TAuction {

    @Id
    private int id;
    @NotNull
    private UUID owner;
    @NotNull
    private int item;
    @NotNull
    @ManyToOne
    private TPlattform plattform;
    private double direct_buy;
    private Date auction_end;
    private double start_bid;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "auction")
    private List<TBid> all_bids = new ArrayList<TBid>();


    public void Auction() {

    }

    public TAuction() {

    }

    public boolean isAuction() {

        return start_bid >= 0;
    }

    public boolean isDirectBuy() {

        return direct_buy >= 0;
    }
}
