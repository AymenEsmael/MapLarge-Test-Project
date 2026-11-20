
CREATE TABLE Promotions
(
    prom_id NUMBER(38,0),
    prom_action VARCHAR(30),
    prom_period VARCHAR(20),
    p_description VARCHAR(50),
    PRIMARY KEY (prom_id)
);

CREATE TABLE Flights
(
    flight_id NUMBER(38,0),
    flight_miles NUMBER(10,2),
    source VARCHAR(30),
    destination VARCHAR(30),
    arrival_datetime VARCHAR(40),
    dept_datetime VARCHAR(40),
    percent_increases_inc NUMBER(3),
    percent_increases_ref NUMBER(3),
    incentive_id NUMBER (38,0),
    referral_id NUMBER(38,0),
    passid NUMBER(38,0) NOT NULL,
    point_account_id NUMBER(38,0) NOT NULL,
    PRIMARY KEY (flight_id),
    FOREIGN KEY (incentive_id) REFERENCES Empl_Incentives(incentive_id),
    FOREIGN KEY (referral_id) REFERENCES Empl_Referrals(referral_id),
    FOREIGN KEY (passid) REFERENCES Passengers(passid),
    FOREIGN KEY (point_account_id) REFERENCES Point_Accounts(point_account_id)
);

CREATE TABLE Promotions_Flight
(
    prom_id NUMBER(38,0),
    flight_id NUMBER(38,0),
    PRIMARY KEY (prom_id,flight_id),
    FOREIGN KEY(prom_id) REFERENCES Promotions(prom_id),
    FOREIGN KEY(flight_id) REFERENCES Flights(flight_id)
);

CREATE TABLE Trips_Flights
(
    flight_id NUMBER(38,0) NOT NULL,
    trip_id NUMBER(38,0),
    PRIMARY KEY (flight_id,trip_id),
    FOREIGN KEY (flight_id) REFERENCES Flights(flight_id),
    FOREIGN KEY (trip_id) REFERENCES Trips (trip_id)
);

CREATE TABLE Trips
(
    trip_id NUMBER(38,0),
    trip_miles NUMBER(10,2),
    source VARCHAR(30),
    destination VARCHAR (30),
    arrival_datetime VARCHAR(40),
    dept_deatetime VARCHAR(40),
    PRIMARY KEY(trip_id)
);

CREATE TABLE Empl_Incentives
(
    incentive_id NUMBER(38,0),
    passenger_id NUMBER(38,0),
    PRIMARY KEY (incentive_id)
);

CREATE TABLE Empl_Referrals
(
    referral_id NUMBER(38,0),
    passenger_id NUMBER(38,0),
    PRIMARY KEY (referral_id)
);

CREATE TABLE Point_Accounts
(
    point_account_id NUMBER(38,0),
    total_points NUMBER(20,0),
    PRIMARY KEY (point_account_id)
);

CREATE TABLE ExchgCenters
(
    center_id NUMBER(38,0),
    center_name VARCHAR(30),
    c_location VARCHAR(50),
    PRIMARY KEY(center_id)
);

CREATE TABLE Login
(
    username VARCHAR(20) NOT NULL,
    password VARCHAR(30),
    passid NUMBER(38,0) UNIQUE NOT NULL,
    PRIMARY KEY(username),
    FOREIGN KEY(passid) REFERENCES Passengers(passid)
);

CREATE TABLE Cards
(
    card_id NUMBER(38,0),
    expiry_date VARCHAR(20),
    is_valid VARCHAR(5),
    c_date VARCHAR(20),
    passid NUMBER(38,0) NOT NULL,
    PRIMARY KEY(card_id),
    FOREIGN KEY(passid) REFERENCES Passengers(passid)
);

CREATE TABLE Awards
(
    award_id NUMBER(38,0),
    a_description VARCHAR(30),
    points_required NUMBER(30,0),
    PRIMARY KEY(award_id)
);

CREATE TABLE Awards_Passengers
(
    redemption_date VARCHAR(20),
    quantity NUMBER(10),
    award_id NUMBER(38,0),
    passid NUMBER(38,0),
    point_account_id NUMBER (38,0) NOT NULL,
    center_id NUMBER(38,0) NOT NULL,
    PRIMARY KEY (award_id,passid),
    FOREIGN KEY (award_id) REFERENCES Awards (award_id),
    FOREIGN KEY (passid) REFERENCES Passengers(passid),
    FOREIGN KEY (point_account_id) REFERENCES Point_Accounts (point_account_id),
    FOREIGN KEY (center_id) REFERENCES ExchgCenters (center_id)
);

CREATE TABLE Passengers
(
    passid NUMBER (38,0),
    pname VARCHAR(20),
    ssn VARCHAR(11),
    is_member VARCHAR(5),
    email VARCHAR(30),
    gender VARCHAR(10),
    occupation VARCHAR(30),
    dob VARCHAR(10),
    PRIMARY KEY (passid)
);

CREATE TABLE Passengers_Phone
(
    phone NUMBER (10,0),
    passid NUMBER(38,0),
    PRIMARY KEY (phone,passid),
    FOREIGN KEY (passid) REFERENCES Passengers (passid)
);

CREATE TABLE Passengers_Address
(
    passid NUMBER (38,0),
    zip NUMBER (38,0),
    home_num NUMBER (5,0),
    street VARCHAR (20),
    city VARCHAR (20),
    state_name VARCHAR(30),
    PRIMARY KEY (passid,zip,home_num,street,city,state_name),
    FOREIGN KEY (passid) REFERENCES Passengers (passid)
);