-- apply changes
create table auction_auctions (
  id                            integer auto_increment not null,
  owner                         varchar(40) not null,
  item                          integer not null,
  plattform_id                  integer not null,
  direct_buy                    double not null,
  auction_end                   datetime(6),
  start_bid                     double not null,
  constraint pk_auction_auctions primary key (id)
);

create table auction_bids (
  id                            integer auto_increment not null,
  auction_id                    integer,
  bid                           double not null,
  bidder                        varchar(40),
  constraint uq_auction_bids_auction_id_bid unique (auction_id,bid),
  constraint pk_auction_bids primary key (id)
);

create table auction_plattforms (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_auction_plattforms primary key (id)
);

create index ix_auction_auctions_plattform_id on auction_auctions (plattform_id);
alter table auction_auctions add constraint fk_auction_auctions_plattform_id foreign key (plattform_id) references auction_plattforms (id) on delete restrict on update restrict;

create index ix_auction_bids_auction_id on auction_bids (auction_id);
alter table auction_bids add constraint fk_auction_bids_auction_id foreign key (auction_id) references auction_auctions (id) on delete restrict on update restrict;

