IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Convert_Identity_To_Sequence_Proc]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[Convert_Identity_To_Sequence_Proc]

GO

CREATE PROCEDURE Convert_Identity_To_Sequence_Proc (
	@SCHEME_NAME NVARCHAR(Max), 
	@TABLE_NAME NVARCHAR(Max), 
	@IDENTITY_COLUMN_NAME NVARCHAR(MAX), 
	@DATA_TYPE_NAME NVARCHAR(MAX) 
) AS

PRINT 'Scheme: '			+ @SCHEME_NAME
PRINT 'Table: '				+ @TABLE_NAME
PRINT 'Identity Column: '	+ @IDENTITY_COLUMN_NAME
PRINT 'Data Type: '			+ @DATA_TYPE_NAME

DECLARE @SQL AS NVARCHAR(MAX)
DECLARE @OBJECTID AS BIGINT
DECLARE @SCHEME_AND_TABLENAME AS NVARCHAR(MAX)

SET @SCHEME_AND_TABLENAME=@SCHEME_NAME + '.' + @TABLE_NAME
PRINT 'scheme and table name: ' + @SCHEME_AND_TABLENAME

--Pick up object ID of the table
SELECT @OBJECTID=OBJECT_ID(@SCHEME_AND_TABLENAME)
PRINT 'ObjectId: ' + cast(@OBJECTID as nvarchar(max))

--Check if the table has an identity table
If (Select Count(*) from sys.identity_columns where object_id =@OBJECTID)=0
BEGIN
RAISERROR('Could not found the identity column in this table',16,1)
RETURN
END

-- Add a new column in the table that does not have the IDENTITY property with the same data type
SET @SQL ='ALTER TABLE ' + @SCHEME_AND_TABLENAME +' ADD ' +@IDENTITY_COLUMN_NAME + 'New ' + @DATA_TYPE_NAME + ' NULL'
Print @SQL
EXEC (@SQL)

-- Copy values from the old column and update into the new column
SET @SQL ='UPDATE ' + @SCHEME_AND_TABLENAME +' SET ' + @IDENTITY_COLUMN_NAME + 'New' + ' =' + @IDENTITY_COLUMN_NAME
Print @SQL
EXEC (@SQL)

-- Drop the old identity column
SET @SQL ='ALTER TABLE ' + @SCHEME_AND_TABLENAME + ' DROP COLUMN ' + @IDENTITY_COLUMN_NAME
Print @SQL
EXEC (@SQL)

-- Rename the new column to the old columns name
SET @SQL ='EXEC sp_rename ' + ''''+ @SCHEME_AND_TABLENAME + '.'+ @IDENTITY_COLUMN_NAME+'New' + '''' + ',' + ''''+ @IDENTITY_COLUMN_NAME + '''' + ',' + '''COLUMN'''
Print @SQL
EXEC (@SQL)

-- Change the new column to NOT NULL
SET @SQL ='ALTER TABLE ' + @SCHEME_AND_TABLENAME + ' ALTER COLUMN ' + @IDENTITY_COLUMN_NAME + ' ' + @DATA_TYPE_NAME + +' NOT NULL'
Print @SQL
EXEC (@SQL)

GO
