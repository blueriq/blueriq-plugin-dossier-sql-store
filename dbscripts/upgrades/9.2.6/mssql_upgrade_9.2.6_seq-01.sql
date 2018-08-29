-- MSSQL upgrade script for the Dossier plugin of Blueriq release 9.2.6
--
-- Adds table in which database updates are tracked.
-- 
-- Needs to be executed on Blueriq databases for versions equal to or before 9.2.5.

CREATE TABLE dossier_Releases (id numeric(19,0) identity not null, description varchar(150) null, releaseDate datetime not null, version varchar(100) not null, primary key (id));
INSERT INTO dossier_Releases (version, releasedate, description) VALUES('9.2.6 seq-01', CURRENT_TIMESTAMP, 'Adds table in which database updates are tracked.');
