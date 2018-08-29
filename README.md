[![][logo]][website] 

# About

The plugin `blueriq-plugin-dossier-sql-store` used to be a part of the Blueriq Runtime but since the release of Blueriq 11, the support of this plugin is discontinued. 
Blueriq released the sources of this plugin with the intention of enabling customers to migrate to Aggregates at their own pace but without support from Blueriq. No rights reserved.

# Build from source

To compile and build war use:

```bash
mvn clean verify -DskipTests
```

To test the war, please add the Blueriq `license.aql` to `src\test\resources` and use:

```bash
mvn clean verify
```

# Run example

Deploy `Runtime.war` to Tomcat container. Create a configuration folder and add Blueriq `license.aql` or package Blueriq `license.aql` by adding it to `src\main\resources`.
Start Tomcat container with the following parameters:

```bash
-Dspring.config.additional-location=file://path_to_conf/ # URI of the configuration folder which contains the Blueriq license.
-Dspring.profiles.active=native,development-tools (embedded H2 database) or 
-Dspring.profiles.active=native,development-tools,externaldatasources (direct connection) or 
-Dspring.profiles.active=native,development-tools,jndidatasources (JNDI)
```

Configure the datasource for the Dossier plugin via either the externaldatasources profile or the jndidatasources profile.

## externaldatasources profile

*application-externaldatasources.properties*

```bash
blueriq.datasource.dossier.url=jdbc:sqlserver://<database_url>:<port>;databaseName=<databaseName>;instance=SQL_EXPRESS
blueriq.datasource.dossier.username=<username>
blueriq.datasource.dossier.password=<password>
blueriq.datasource.dossier.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
blueriq.hibernate.dossier.hbm2ddl.auto=validate
blueriq.hibernate.dossier.dialect=org.hibernate.dialect.SQLServer2012Dialect
```

## jndidatasources profile

*application-jndidatasources.properties*

```bash
blueriq.datasource.dossier.jndiName=java:/comp/env/jdbc/main
blueriq.hibernate.dossier.hbm2ddl.auto=validate
blueriq.hibernate.dossier.dialect=org.hibernate.dialect.SQLServer2012Dialect
```

## Supported dialects

- org.hibernate.dialect.SQLServer2012Dialect (for SQL Server)
- org.hibernate.dialect.Oracle10gDialect (for Oracle)
- org.hibernate.dialect.H2Dialect (for H2) (we do not recommend using a H2 database in production environments)

## Supported hbm2ddl.auto

- none
- validate

## Database scripts

Database scripts can be found in `dbscripts`.

# Studio service and container types

## AQ_DossierList container

![][dossierlist_general]
---
![][dossierlist_param_1]
---
![][dossierlist_param_2]
---
![][dossierlist_param_3]
---
![][dossierlist_param_4]
---
![][dossierlist_param_5]
---
![][dossierlist_param_6]
---
![][dossierlist_param_7]
---
![][dossierlist_param_8]
---
![][dossierlist_param_9]

## AQ_DossierManager service

![][dossiermanager_general]
---
![][dossiermanager_param_1]
---
![][dossiermanager_param_2]
---
![][dossiermanager_param_3]
---
![][dossiermanager_param_4]
---
![][dossiermanager_param_5]
---
![][dossiermanager_param_6]
---
![][dossiermanager_param_7]
---
![][dossiermanager_param_8]
---
![][dossiermanager_param_9]
---
![][dossiermanager_param_10]
---
![][dossiermanager_param_11]

[dossierlist_general]: images/aq_dossierlist/dossierlist_general.png
[dossierlist_param_1]: images/aq_dossierlist/dossierlist_parameters_1.png
[dossierlist_param_2]: images/aq_dossierlist/dossierlist_parameters_2.png
[dossierlist_param_3]: images/aq_dossierlist/dossierlist_parameters_3.png
[dossierlist_param_4]: images/aq_dossierlist/dossierlist_parameters_4.png
[dossierlist_param_5]: images/aq_dossierlist/dossierlist_parameters_5.png
[dossierlist_param_6]: images/aq_dossierlist/dossierlist_parameters_6.png
[dossierlist_param_7]: images/aq_dossierlist/dossierlist_parameters_7.png
[dossierlist_param_8]: images/aq_dossierlist/dossierlist_parameters_8.png
[dossierlist_param_9]: images/aq_dossierlist/dossierlist_parameters_9.png

[dossiermanager_general]: images/aq_dossiermanager/dossiermanager_general.png
[dossiermanager_param_1]: images/aq_dossiermanager/dossiermanager_parameters_1.png
[dossiermanager_param_2]: images/aq_dossiermanager/dossiermanager_parameters_2.png
[dossiermanager_param_3]: images/aq_dossiermanager/dossiermanager_parameters_3.png
[dossiermanager_param_4]: images/aq_dossiermanager/dossiermanager_parameters_4.png
[dossiermanager_param_5]: images/aq_dossiermanager/dossiermanager_parameters_5.png
[dossiermanager_param_6]: images/aq_dossiermanager/dossiermanager_parameters_6.png
[dossiermanager_param_7]: images/aq_dossiermanager/dossiermanager_parameters_7.png
[dossiermanager_param_8]: images/aq_dossiermanager/dossiermanager_parameters_8.png
[dossiermanager_param_9]: images/aq_dossiermanager/dossiermanager_parameters_9.png
[dossiermanager_param_10]: images/aq_dossiermanager/dossiermanager_parameters_10.png
[dossiermanager_param_11]: images/aq_dossiermanager/dossiermanager_parameters_11.png

[logo]: https://www.blueriq.com/wp-content/uploads/2018/07/BLUERIQ-rgb-logo-kleur-gradient-PNG-300x111.png
[website]: http://www.blueriq.com