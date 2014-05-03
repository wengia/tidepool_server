-- Add table in schemas javaProject1CarDB

create schema if not exists tidepool;

drop table if exists tidepool.friends;
drop table if exists tidepool.myData;
drop table if exists tidepool.myUser;
drop table if exists tidepool.contact;


create table if not exists tidepool.myUser (
	id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    pwd VARCHAR(500) NOT NULL,
    phone VARCHAR(20),
    birth DATE, 
    gender VARCHAR(10),
    role VARCHAR(10) NOT NULL,
    location_lat DOUBLE,
    location_lng DOUBLE,
	PRIMARY KEY (id)
);

create table if not exists tidepool.myData (
	id INT NOT NULL AUTO_INCREMENT,
    theTime DATETIME NOT NULL,
    BG INTEGER NOT NULL,
    insulin INTEGER NOT NULL,
    uid INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (uid)
	  REFERENCES myUser(id)
	  ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE (theTime, uid)
);

create table if not exists tidepool.friends (
	id INT Not NULL AUTO_INCREMENT,
	uid1 INT Not NULL,
	uid2 INT Not NULL,
	
	PRIMARY KEY (id),
	INDEX (uid1),
    INDEX (uid2),

	FOREIGN KEY (uid1)
	  REFERENCES myUser(id)
	  ON UPDATE CASCADE ON DELETE CASCADE,

	FOREIGN KEY (uid2)
      REFERENCES myUser(id)
	  ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE (uid1, uid2)
);

create table if not exists tidepool.contact (
	contact_id INT Not NULL AUTO_INCREMENT,
	sender INT Not NULL,
	receiver INT Not NULL,
	theStatus VARCHAR(10),

	PRIMARY KEY (contact_id),

	FOREIGN KEY (sender)
	  REFERENCES myUser(id)
	  ON UPDATE CASCADE ON DELETE CASCADE,

	FOREIGN KEY (receiver)
      REFERENCES myUser(id)
	  ON UPDATE CASCADE ON DELETE CASCADE,

	UNIQUE (sender, receiver)
);


insert ignore into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (1 ,'dummy1@gmail.com', 'dummy1', 'pwd', '1234567890', '1985-07-06', 'female', 'patient');
insert ignore into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (2, 'dummy2@gmail.com', 'dummy2', 'pwd', '1234567890', '1991-07-08', 'male', 'patient');
insert ignore into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (3, 'dummy3@gmail.com', 'dummy3', 'pwd', '1234567890', '1997-04-06', 'female', 'patient');
insert ignore into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (4, 'dummy4@gmail.com', 'dummy4', 'pwd', '1234567890', '1956-07-21', 'female', 'parent');
insert ignore into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (5, 'dummy5@gmail.com', 'dummy5', 'pwd', '1234567890', '1991-12-06', 'male', 'patient');
insert into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (6, 'wenjia.ma@west.cmu.edu', 'wenjia', 'pwd', '6504229820', '1990-07-06', 'female', 'patient');
insert into tidepool.myUser (id, email, username, pwd, phone, birth, gender, role ) 
value (7 ,'jingyi.li@west.cmu.edu', 'jingyi', 'pwd', '4129805715', '1975-04-06', 'female', 'parent');


-- dummy data for dummy0
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:05', 28, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:10', 25, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:15', 30, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:20', 35, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:25', 47, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:30', 100, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:35', 150, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:40', 170, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:45', 180, 10, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:50', 200, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:55', 150, 0, 5);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 8:00', 110, 0, 5);

-- dummy Data for dummy1
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:15', 30, 0, 1);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:20', 35, 0, 1);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:25', 47, 0, 1);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:30', 100, 0, 1);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:35', 150, 0, 1);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:40', 170, 7, 1);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:45', 180, 0, 1);

-- dummy data for dummy2
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:00', 50, 0, 2);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:05', 28, 0, 2);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:10', 25, 0, 2);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:15', 70, 0, 2);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:20', 100, 0, 2);

-- dummy data for dummy3
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:25', 47, 0, 3);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:30', 100, 0, 3);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:35', 150, 0, 3);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:40', 170, 15, 3);
insert ignore into tidepool.myData (theTime, BG, insulin, uid) 
value ('2014-04-22 7:45', 180, 0, 3);

-- friends table
insert ignore into tidepool.friends (uid1, uid2) value (2, 4);
insert ignore into tidepool.friends (uid1, uid2) value (3, 4);
insert ignore into tidepool.friends (uid1, uid2) value (4, 5);
