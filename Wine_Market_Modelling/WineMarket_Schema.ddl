--SCHEMA MODELLING ELEMENTS OF THE WINE MARKET
--(provided by instructor Manos Pappagelis, University of Toronto)	

DROP TABLE countries CASCADE;
DROP TABLE winemakers CASCADE;
DROP TABLE winecolours CASCADE;
DROP TABLE ratings CASCADE;
DROP TABLE merchants CASCADE;
DROP TABLE pricelist CASCADE;
DROP TABLE wine CASCADE;

-- The countries table contains countries that produce wine.
CREATE TABLE countries (
    cid INTEGER PRIMARY KEY,
    cname VARCHAR(20) NOT NULL);
    
-- The winemaker table contains information of winemakers.
CREATE TABLE winemakers (
    wmid INTEGER PRIMARY KEY,
    wmname VARCHAR(20) NOT NULL,
    cid INTEGER REFERENCES countries(cid) ON DELETE RESTRICT);

-- The winecolours table contains the colours of wine.
-- wcname is one of "Red", "White" or "Rose".
CREATE TABLE winecolours (
    wcid INTEGER PRIMARY KEY,
    wcname VARCHAR(20) NOT NULL);
    
-- The ratings table contains ratings of wine.
-- rating is an integer between 1 and 5, 5 for the best.
CREATE TABLE ratings (
    rid INTEGER PRIMARY KEY,
    rating INTEGER NOT NULL,
    description VARCHAR(20) NOT NULL);
    
-- The wine table contains information about wine.
-- wyear is the year of production.
-- bestbeforeny is the number of years that the wine is best. 
-- For example, if a wine is produced in 2005 and bestbeforeny = 5, 
-- then it is best to be consumed from January 1 2005 to December 31 2010.
-- msrp is the manufacturer suggested retail price (in dollars).
CREATE TABLE wine (
    wid INTEGER PRIMARY KEY,
    wcid INTEGER REFERENCES winecolours(wcid) ON DELETE RESTRICT,
    rid INTEGER REFERENCES ratings(rid) ON DELETE RESTRICT,
    wmid INTEGER REFERENCES winemakers(wmid) ON DELETE RESTRICT,
    wname VARCHAR(20) NOT NULL,
    wyear INTEGER NOT NULL,
    bestbeforeny INTEGER NOT NULL,
    msrp DOUBLE PRECISION NOT NULL);
    
-- The merchants table contains names of merchants for wine.
-- mname is the name of the merchant, such as, "Wine Rack", "WineOnline", etc.
CREATE TABLE merchants (
    mid INTEGER PRIMARY KEY,
    mname VARCHAR(20) NOT NULL);
    
-- The pricelist table contains the merchant price of wines.
-- startmonth and endmonth are between 1 and 12.
-- startyear is greater than the production year of the wine.
CREATE TABLE pricelist (
    wid INTEGER REFERENCES wine(wid) ON DELETE RESTRICT,
    mid INTEGER REFERENCES merchants(mid) ON DELETE RESTRICT,
    startyear INTEGER NOT NULL,
    startmonth INTEGER NOT NULL,
    endyear INTEGER NOT NULL,
    endmonth INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (wid, mid));


