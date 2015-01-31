package de.raidcraft.auction.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "auction_bids", uniqueConstraints =
@UniqueConstraint(columnNames = {"auction_id", "bid"}))
public class TBid {


    @Id
    private int id;
    @ManyToOne
    private TAuction auction;
    private double bid;
    private UUID bidder;
}
