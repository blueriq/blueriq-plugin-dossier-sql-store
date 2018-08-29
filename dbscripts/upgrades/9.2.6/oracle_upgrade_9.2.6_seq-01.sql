-- Oracle upgrade script for the Dossier plugin of Blueriq release 9.2.6
--
-- Adds table in which database updates are tracked.
-- 
-- Needs to be executed on Blueriq databases for versions equal to or before 9.2.5.

CREATE TABLE dossier_Releases (id number(19,0) not null, description varchar2(150 char), releaseDate timestamp not null, version varchar2(100 char) not null, primary key (id));
INSERT INTO dossier_Releases (id, version, releasedate, description) VALUES(S_dossier_releaseId.NEXTVAL, '9.2.6 seq-01', SYSDATE, 'Adds table in which database updates are tracked.');
