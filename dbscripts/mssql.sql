/** 
 * Create Tables 
 */ 
CREATE TABLE [aq_dossier] 
  ( 
     [id]         BIGINT NOT NULL, 
     [date1]      DATETIME2, 
     [date2]      DATETIME2, 
     [feature1]   NVARCHAR(50), 
     [feature2]   NVARCHAR(50), 
     [feature3]   NVARCHAR(50), 
     [feature4]   NVARCHAR(50), 
     [profilexml] NVARCHAR(max) NOT NULL, 
     [type]       NVARCHAR(255), 
	 
     CONSTRAINT [pk_aq_dossier] PRIMARY KEY (
		[id]
	) 
  ); 

CREATE TABLE [dossier_releases] 
  ( 
     [id]          BIGINT NOT NULL, 
     [description] NVARCHAR(150), 
     [releasedate] DATETIME2 NOT NULL, 
     [version]     NVARCHAR(100) NOT NULL, 
	 
     CONSTRAINT [pk_dossier_releases] PRIMARY KEY (
		[id]
	) 
  ); 

/** 
 * Create sequences 
 */
CREATE SEQUENCE [s_dossier_releaseid] AS BIGINT START WITH 1 INCREMENT BY 1 MINVALUE 0;

CREATE SEQUENCE [hibernate_sequence] AS BIGINT START WITH 1 INCREMENT BY 1 MINVALUE 0;
 
/** 
 * Insert release record 
 */
INSERT INTO [dossier_releases] 
            ([id],
			 [version], 
             [releasedate], 
             [description]) 
VALUES     (NEXT VALUE FOR [s_dossier_releaseid],
			'10.0.0', 
            CURRENT_TIMESTAMP, 
            'Initial creation');