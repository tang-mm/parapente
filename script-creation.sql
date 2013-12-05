
--
-- Database Table Creation
--

DROP TABLE IF EXISTS Flight;
DROP TABLE IF EXISTS Point;
 

CREATE TABLE Flight (  
  idf BIGINT NOT NULL, -- AUTO_INCREMENT,
  datef DATE NOT NULL,
  pilot VARCHAR,
  type VARCHAR, 
  model VARCHAR,
  PRIMARY KEY(idf)
);

CREATE UNIQUE INDEX FlightIndex1
	   ON Flight (idf);
CREATE UNIQUE INDEX FlightIndex2
	   ON Flight (datef);


CREATE TABLE Point (
  idp BIGINT NOT NULL,
  idf BIGINT NOT NULL,
  time TIME NOT NULL,
  lat DOUBLE NOT NULL,
  lng DOUBLE NOT NULL,
  alt INTEGER NOT NULL,
  geohash VARCHAR,
  vlat DOUBLE,
  vlng DOUBLE,
  valt  DOUBLE, 
  PRIMARY KEY(idp, idf),
  FOREIGN KEY(idf) REFERENCES Flight(idf)
);

CREATE UNIQUE INDEX PointIndex1
	   ON Point (idp, idf);
CREATE UNIQUE INDEX PointIndex2
	   ON Point (geohash);
