--***************************************************************************************************
--    Script	:	Update plugin-dossier-sql script 02
--
--    Author	:	Blueriq
--    Date		:	3-3-2017
--    Release	:	10.0.0
--  
--***************************************************************************************************

-- Note: In script 01, the column aq_dossier.profilexml is changed from CLOB to NCLOB.
-- The old data is stored in aq_dossier.profilexml_old. Please make sure everything works
-- before executing this script.

--***************************************************************************************************
--*   Altering table: aq_dossier
--***************************************************************************************************

ALTER TABLE aq_dossier 
  DROP COLUMN profilexml_old;

--***************************************************************************************************
--*   Insert statements
--***************************************************************************************************
INSERT INTO dossier_releases (
	id, version, releasedate, description
) VALUES (
	s_dossier_releaseid.NEXTVAL, '10.0.0', CURRENT_TIMESTAMP, 'Drop column aq_dossier.profilexml_old'
);

COMMIT;

--***************************************************************************************************
--*   End of script
--***************************************************************************************************