package com.aquima.plugin.dossier.data;

import com.aquima.plugin.dossier.config.DossierConfig;

import com.blueriq.component.api.datasource.EmbeddedCondition;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
@Conditional(EmbeddedCondition.class)
public class DossierEmbeddedConfig {

  @Bean(name = DossierConfig.TRANSACTION_MANAGER_NAME)
  public HibernateTransactionManager dossierTransactionManager(
      @Qualifier(DossierConfig.SESSION_FACTORY_NAME) SessionFactory sessionFactory) {
    return new HibernateTransactionManager(sessionFactory);
  }

  @Bean(name = DossierConfig.SESSION_FACTORY_NAME)
  public LocalSessionFactoryBean dossierSessionFactory(
      @Qualifier(DossierConfig.DATA_SOURCE_NAME) DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan(DossierConfig.ENTITY_BASE_PACKAGE);
    sessionFactory.setHibernateProperties(hibernateProperties());
    return sessionFactory;
  }

  private Properties hibernateProperties() {
    Properties result = new Properties();
    result.setProperty("hibernate.hbm2ddl.auto", "create");
    result.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    return result;
  }

  @Bean(name = DossierConfig.DATA_SOURCE_NAME)
  public DataSource dossierDataSource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

}
