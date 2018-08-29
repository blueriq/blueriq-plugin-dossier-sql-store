package com.aquima.plugin.dossier.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.aquima.plugin.dossier.repository" })
public class DataTestConfig {

  @Bean(name = DossierConfig.TRANSACTION_MANAGER_NAME)
  public HibernateTransactionManager transactionManager(
      @Qualifier(DossierConfig.SESSION_FACTORY_NAME) SessionFactory sessionFactory) {
    return new HibernateTransactionManager(sessionFactory);
  }

  @Bean(name = DossierConfig.SESSION_FACTORY_NAME)
  public LocalSessionFactoryBean sessionFactory(@Qualifier(DossierConfig.DATA_SOURCE_NAME) DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan(DossierConfig.ENTITY_BASE_PACKAGE);
    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto", "create");
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    properties.setProperty("hibernate.cache.use_second_level_cache", "false");
    properties.setProperty("hibernate.id.new_generator_mappings", "false");
    properties.setProperty("hibernate.use_nationalized_character_data", "true");
    properties.setProperty("hibernate.show_sql", "true");
    sessionFactory.setHibernateProperties(properties);
    return sessionFactory;
  }

  @Bean(name = DossierConfig.DATA_SOURCE_NAME)
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder() //
        .setType(EmbeddedDatabaseType.H2) //
        .addScript("h2.sql") //
        .build();
  }
}
