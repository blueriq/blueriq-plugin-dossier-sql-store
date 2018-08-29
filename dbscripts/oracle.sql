-- *********************
-- *** Create Tables ***
-- *********************
CREATE TABLE aq_dossier 
  ( 
     id         NUMBER(19, 0) NOT NULL, 
     date1      TIMESTAMP, 
     date2      TIMESTAMP, 
     feature1   NVARCHAR2(50), 
     feature2   NVARCHAR2(50), 
     feature3   NVARCHAR2(50), 
     feature4   NVARCHAR2(50), 
     profilexml NCLOB NOT NULL, 
     type       NVARCHAR2(255), 
	 
     CONSTRAINT pk_aq_dossier PRIMARY KEY (
		id
	) 
  )
/

CREATE TABLE dossier_releases 
  ( 
     id          NUMBER(19, 0) NOT NULL, 
     description NVARCHAR2(150), 
     releasedate TIMESTAMP NOT NULL, 
     version     NVARCHAR2(100) NOT NULL, 
	 
     CONSTRAINT pk_dossier_releases PRIMARY KEY (
		id
	) 
  )
/

-- ************************
-- *** Create sequences ***
-- ************************
DECLARE
  seed_aq_dossier NUMBER;
  seed_dossier_releases NUMBER;
BEGIN
  SELECT (CASE WHEN MAX(id) IS NULL THEN 0 ELSE MAX(id) END)+1 INTO seed_aq_dossier FROM aq_dossier;
  SELECT (CASE WHEN MAX(id) IS NULL THEN 0 ELSE MAX(id) END)+1 INTO seed_dossier_releases FROM dossier_releases;
  
  DBMS_OUTPUT.put_line('seed_aq_dossier '|| seed_aq_dossier);
  DBMS_OUTPUT.put_line('seed_dossier_releases '|| seed_dossier_releases);
  
  
  EXECUTE IMMEDIATE 'CREATE SEQUENCE s_dossier_releaseid MINVALUE 0 START WITH '|| seed_dossier_releases ||' INCREMENT BY 1';
  EXECUTE IMMEDIATE 'CREATE SEQUENCE hibernate_sequence MINVALUE 0 START WITH '|| seed_aq_dossier ||' INCREMENT BY 1';
END;
/

-- *****************************
-- *** Insert release record ***
-- *****************************
INSERT INTO dossier_releases 
            (id, 
             version, 
             releasedate, 
             description) 
VALUES     (s_dossier_releaseid.NEXTVAL, 
            '10.0.0', 
            SYSDATE, 
            'Initial creation')
/