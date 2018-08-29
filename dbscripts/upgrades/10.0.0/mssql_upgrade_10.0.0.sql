/***************************************************************************************************
*    Script		:	Update plugin-dossier-sql-store
*
*    Author		:	Blueriq
*    Date		:	3-3-2017
*    Release	:	10.0.0
*  
***************************************************************************************************/
 
SET ANSI_NULLS ON
GO
 
SET QUOTED_IDENTIFIER OFF
GO
/***************************************************************************************************
*   Declare variables
***************************************************************************************************/

DECLARE	@scheme								varchar(80)		= 'dbo',
		@message							varchar(80)		= 'ERROR occurred in ',
		@start_seq_aq_dossier_id			numeric(19,0)	= (SELECT (CASE WHEN MAX([id]) IS NULL THEN 0 ELSE MAX([id]) END)+1 FROM aq_dossier),
		@start_seq_dossier_releases_id		numeric(19,0)	= (SELECT (CASE WHEN MAX([id]) IS NULL THEN 0 ELSE MAX([id]) END)+1 FROM dossier_releases),
		@table_name 						varchar(255), 
		@constraint_name 					varchar(255);

PRINT 'var: scheme: '							+ @scheme
PRINT 'var: start_seq_aq_dossier_id: '			+ CAST(@start_seq_aq_dossier_id AS VARCHAR)
PRINT 'var: start_seq_dossier_releases_id: '	+ CAST(@start_seq_dossier_releases_id AS VARCHAR)

/***************************************************************************************************
*   Processing
***************************************************************************************************/
PRINT '**** Updating blueriq-component-plugin-dossier-sql-store to 10.0.0 ****'
BEGIN TRY

	PRINT ''
	PRINT 'BEGIN Transaction.'
	PRINT ''

    BEGIN TRAN TRAN_10_0_0
	
    BEGIN		
		/***************************************************************************************************
		*   Dropping all constraints
		***************************************************************************************************/
		PRINT	'-- Dropping all constraints: plugin-dossier-sql-store --'
			
		DECLARE CurName CURSOR FAST_FORWARD READ_ONLY
		FOR
		SELECT o.[name] as table_name, i.[name] as index_name FROM [sys].[indexes] i
		INNER JOIN [sys].[objects] o ON i.[object_id] = o.[object_id]
		WHERE o.[name] IN (
			UPPER('aq_dossier'), 
			UPPER('dossier_releases')
		) and (i.[type] = 1 or (i.[type] = 2 and is_unique_constraint = 1)) -- PK's or UK's

		OPEN CurName

		FETCH NEXT FROM CurName INTO @table_name, @constraint_name

		WHILE @@FETCH_STATUS = 0
		BEGIN
			EXEC ('ALTER TABLE [' + @table_name + '] DROP CONSTRAINT [' + @constraint_name + '];');

			FETCH NEXT FROM CurName INTO @table_name, @constraint_name
		END

		CLOSE CurName
		DEALLOCATE CurName
		
 		PRINT	'-- Finished dropping all constraints: plugin-dossier-sql-store-store --'
		PRINT	''
			
		/***************************************************************************************************
		*   Creating sequences
		***************************************************************************************************/
		PRINT	'-- Creating sequences: plugin-dossier-sql-store --'
		EXEC ('CREATE SEQUENCE [hibernate_sequence] 
			AS [BIGINT] START WITH '+ @start_seq_aq_dossier_id +' INCREMENT BY 1 MINVALUE 0;');

		EXEC ('CREATE SEQUENCE [s_dossier_releaseid] 
			AS [BIGINT] START WITH '+ @start_seq_dossier_releases_id +' INCREMENT BY 1 MINVALUE 0;');
				
 		PRINT	'-- Finished creating sequences: plugin-dossier-sql-store --'
		PRINT	''
		
		/***************************************************************************************************
		*   Altering table: aq_dossier
		***************************************************************************************************/
		PRINT	'-- Altering table: aq_dossier --'
		EXEC dbo.Convert_Identity_To_Sequence_Proc @scheme, 'aq_dossier', 'id', 'BIGINT';

		ALTER TABLE [aq_dossier] 
		  ALTER COLUMN [date1] DATETIME2;
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [date2] DATETIME2;
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [feature1] NVARCHAR(50);
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [feature2] NVARCHAR(50); 
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [feature3] NVARCHAR(50); 
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [feature4] NVARCHAR(50); 
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [profilexml] NVARCHAR(max) NOT NULL;
		 ALTER TABLE [aq_dossier] 
			  ALTER COLUMN [type] NVARCHAR(255);  
			  
 		PRINT	'-- Finished table: aq_dossier --'
		PRINT	''
		
		/***************************************************************************************************
		*   Altering table: dossier_releases
		***************************************************************************************************/
		PRINT	'-- Altering table: dossier_releases --'
		EXEC dbo.Convert_Identity_To_Sequence_Proc @scheme, 'dossier_releases', 'id', 'BIGINT';

		ALTER TABLE [dossier_releases] 
			ALTER COLUMN [description] NVARCHAR(150);
		ALTER TABLE [dossier_releases] 
			ALTER COLUMN [releasedate] DATETIME2 NOT NULL;
		ALTER TABLE [dossier_releases] 
			ALTER COLUMN [version] NVARCHAR(100) NOT NULL; 
			  
 		PRINT	'-- Finished table: dossier_releases --'
		PRINT	''		
		
		/***************************************************************************************************
		*   Creating all constraints
		***************************************************************************************************/
		PRINT	'-- Creating all constraints: plugin-dossier-sql-store --'
		ALTER TABLE [aq_dossier] 
		  ADD CONSTRAINT [pk_aq_dossier] PRIMARY KEY ([id]);
		ALTER TABLE [dossier_releases] 
		  ADD CONSTRAINT [pk_dossier_releases] PRIMARY KEY ([id]);
		  
		/***************************************************************************************************
		*   Insert statements
		***************************************************************************************************/
		INSERT INTO [dossier_releases] (
			[id], [version], [releasedate], [description]
		) VALUES (
			NEXT VALUE FOR [s_dossier_releaseid], '10.0.0', CURRENT_TIMESTAMP, 'Update to 10.0.0.'
		);
	END

END TRY

BEGIN CATCH

    SET     @message = @message + 'TRY/CATCH block.'
    PRINT   'Error on line:   ' + CONVERT(varchar(10), ERROR_LINE())
    PRINT   'Error message:   ' + ERROR_MESSAGE()
    PRINT   'Error number:    ' + CONVERT(varchar(10), ERROR_NUMBER())
    PRINT   'Error severity:  ' + CONVERT(varchar(10), ERROR_SEVERITY())
    PRINT   'Error state:     ' + CONVERT(varchar(10), ERROR_STATE())
    GOTO    ERROR_SECTION

END CATCH
/***************************************************************************************************
*   Commit
***************************************************************************************************/
SET NOCOUNT ON
COMMIT TRAN
PRINT 'Committing transaction.'
PRINT ''

IF (@@TRANCOUNT != 0)
BEGIN
      SET   @message = @message + 'the number of transactions'
      GOTO  ERROR_SECTION
END

PRINT 'The script has ended normally.'

GOTO END_SECTION
 
/***************************************************************************************************
*   Error handling
***************************************************************************************************/
ERROR_SECTION:
      ROLLBACK TRAN
      PRINT 'ROLLBACK transaction.'
      RAISERROR (@message, 16, 1)
      PRINT 'The script has ended in ERROR!'
      PRINT ''
 
END_SECTION:
      PRINT 'End of script.'
 
GO

/***************************************************************************************************
*   End of script
***************************************************************************************************/