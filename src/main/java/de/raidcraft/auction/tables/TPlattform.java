package de.raidcraft.auction.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "auction_plattforms")
public class TPlattform {

    @Id
    private int id;
    private String name;
}
