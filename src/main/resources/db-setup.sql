-- Use this script to set up your Planetarium database

-- needed for referential integrity enforcement
PRAGMA foreign_keys = 1;

create table users(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	username varchar(20) unique,
	password varchar(20)
);

create table planets(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name varchar(20),
	ownerId int references users(id)
);

create table moons(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name varchar(20),
	myPlanetId int references planets(id)
);