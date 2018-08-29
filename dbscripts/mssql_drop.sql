/** 
 * Drop sequences
 */
IF Object_id('s_dossier_releaseid', 'SO') IS NOT NULL 
	DROP SEQUENCE [s_dossier_releaseid];
IF Object_id('hibernate_sequence', 'SO') IS NOT NULL 
	DROP SEQUENCE [hibernate_sequence];

/** 
 * Drop tables
 */
IF Object_id('aq_dossier', 'U') IS NOT NULL 
	DROP TABLE [aq_dossier];
IF Object_id('dossier_Releases', 'U') IS NOT NULL 
	DROP TABLE [dossier_Releases];
