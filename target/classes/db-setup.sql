-- Use this script to set up your Planetarium database

-- needed for referential integrity enforcement
PRAGMA foreign_keys = 1;

create table users(
	id serial primary key,
	username varchar(20) unique,
	password varchar(20)
);

create table planets(
	id serial primary key,
	name varchar(20),
	ownerId int references users(id)
);

create table moons(
	id serial primary key,
	name varchar(20),
	myPlanetId int references planets(id)
);