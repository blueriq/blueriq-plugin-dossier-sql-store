-- **********************
-- *** Drop sequences ***
-- **********************
DECLARE
  COUNT_SEQUENCES INTEGER;
BEGIN
  SELECT COUNT(*) INTO COUNT_SEQUENCES
    FROM USER_SEQUENCES
    WHERE SEQUENCE_NAME = upper('s_dossier_releaseid');
    
  IF COUNT_SEQUENCES > 0 THEN
    EXECUTE IMMEDIATE 'DROP SEQUENCE s_dossier_releaseid';
  END IF;


  SELECT COUNT(*) INTO COUNT_SEQUENCES
    FROM USER_SEQUENCES
    WHERE SEQUENCE_NAME = upper('hibernate_sequence');
    
  IF COUNT_SEQUENCES > 0 THEN
    EXECUTE IMMEDIATE 'DROP SEQUENCE hibernate_sequence';
  END IF;
END;
/

-- *******************
-- *** Drop tables ***
-- *******************
DECLARE
  COUNT_TABLES INTEGER;
BEGIN
  SELECT COUNT(*) INTO COUNT_TABLES
    FROM USER_TABLES
    WHERE TABLE_NAME = upper('aq_dossier');
    
  IF COUNT_TABLES > 0 THEN
    EXECUTE IMMEDIATE 'DROP TABLE aq_dossier CASCADE CONSTRAINTS';
  END IF;


  SELECT COUNT(*) INTO COUNT_TABLES
    FROM USER_TABLES
    WHERE TABLE_NAME = upper('dossier_releases');
    
  IF COUNT_TABLES > 0 THEN
    EXECUTE IMMEDIATE 'DROP TABLE dossier_releases CASCADE CONSTRAINTS';
  END IF;
END;
/