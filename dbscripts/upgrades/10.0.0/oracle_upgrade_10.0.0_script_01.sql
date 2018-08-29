--***************************************************************************************************
--    Script		:	Update plugin-dossier-sql-store script 01
--	  Prerequisites	:	All upgrade scripts from R9 should be executed
--    Author		:	Blueriq
--    Date			:	3-3-2017
--    Release		:	10.0.0
--  
--***************************************************************************************************

--***************************************************************************************************
--*   Altering table: aq_dossier
--***************************************************************************************************

ALTER TABLE aq_dossier 
  MODIFY feature1 NVARCHAR2(50); 
ALTER TABLE aq_dossier 
  MODIFY feature2 NVARCHAR2(50); 
ALTER TABLE aq_dossier 
  MODIFY feature3 NVARCHAR2(50); 
ALTER TABLE aq_dossier 
  MODIFY feature4 NVARCHAR2(50);
ALTER TABLE aq_dossier 
  MODIFY type NVARCHAR2(255);
	 
ALTER TABLE aq_dossier 
  RENAME COLUMN profilexml TO profilexml_old;
ALTER TABLE aq_dossier 
  ADD profilexml NCLOB;

UPDATE aq_dossier SET profilexml = profilexml_old;

ALTER TABLE aq_dossier 
  MODIFY profilexml NOT NULL;

--***************************************************************************************************
--*   Altering table: dossier_releases
--***************************************************************************************************

ALTER TABLE dossier_releases 
  MODIFY description NVARCHAR2(150); 
ALTER TABLE dossier_releases 
  MODIFY version NVARCHAR2(100); 

--***************************************************************************************************
--*   Rename all primary keys
--***************************************************************************************************

COLUMN constraint_name new_val c; 
SELECT constraint_name 
  FROM user_constraints 
  WHERE table_name = UPPER('aq_dossier') 
  AND constraint_type = 'P'; 
  
ALTER TABLE aq_dossier RENAME CONSTRAINT &c TO pk_aq_dossier;
ALTER INDEX &c RENAME TO pk_aq_dossier;

COLUMN constraint_name new_val c; 
SELECT constraint_name 
  FROM user_constraints 
  WHERE table_name = UPPER('dossier_releases') 
  AND constraint_type = 'P'; 
  
ALTER TABLE dossier_releases RENAME CONSTRAINT &c TO pk_dossier_releases;
ALTER INDEX &c RENAME TO pk_dossier_releases;

--***************************************************************************************************
--*   Insert statements
--***************************************************************************************************
INSERT INTO dossier_releases (
	id, version, releasedate, description
) VALUES (
	s_dossier_releaseid.NEXTVAL, '10.0.0', CURRENT_TIMESTAMP, 'Update to 10.0.0.'
);

COMMIT;

--***************************************************************************************************
--*   End of script
--***************************************************************************************************