package de.raidcraft.auction.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Entity
@Table(name = "auction_bids")
public class TBid {

    @Getter
    @Setter
    @Id
    private int id;
    @Getter
    @Setter
    private TAuction auction;
    @Getter
    @Setter
    private double bid;
    @Getter
    @Setter
    private UUID bidder;
}
