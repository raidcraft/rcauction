package de.raidcraft.auction.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dragonfire
 */
@Entity
@Table(name = "auction_plattforms")
public class TPlattform {

    @Getter
    @Setter
    @Id
    private int id;
    @Getter
    @Setter
    private String name;
}
