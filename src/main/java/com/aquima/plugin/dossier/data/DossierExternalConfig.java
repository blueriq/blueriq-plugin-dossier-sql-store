package com.aquima.plugin.dossier.data;

import com.aquima.plugin.dossier.config.DossierConfig;

import com.blueriq.component.api.datasource.DataSourceProfile;
import com.blueriq.component.api.datasource.DatasourcePropertiesUtil;
import com.blueriq.component.api.datasource.IBlueriqDataSourceBuilder;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Profile(DataSourceProfile.EXTERNAL_DATASOURCE_PROFILE_NAME)
public class DossierExternalConfig {

  @Autowired
  private ConfigurableEnvironment configurableEnv;

  @Autowired
  private IBlueriqDataSourceBuilder dataSourceBuilder;

  @Bean(name = DossierConfig.TRANSACTION_MANAGER_NAME)
  public HibernateTransactionManager dossierTransactionManager(
      @Qualifier(DossierConfig.SESSION_FACTORY_NAME) SessionFactory sessionFactory) {
    return new HibernateTransactionManager(sessionFactory);
  }

  @RefreshScope
  @Bean(name = DossierConfig.SESSION_FACTORY_NAME)
  public LocalSessionFactoryBean dossierSessionFactory(
      @Qualifier(DossierConfig.DATA_SOURCE_NAME) DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan(DossierConfig.ENTITY_BASE_PACKAGE);
    sessionFactory.setHibernateProperties(
        DatasourcePropertiesUtil.getHibernateProperties(configurableEnv, DossierConfig.PLUGIN_NAME));
    return sessionFactory;
  }

  @RefreshScope
  @Bean(name = DossierConfig.DATA_SOURCE_NAME)
  public DataSource dossierDataSource() {
    return dataSourceBuilder.buildDataSource(DossierConfig.PLUGIN_NAME);
  }
}
