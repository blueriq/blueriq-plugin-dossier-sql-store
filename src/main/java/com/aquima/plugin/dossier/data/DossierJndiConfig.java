package com.aquima.plugin.dossier.data;

import com.aquima.interactions.foundation.logging.LogFactory;
import com.aquima.interactions.foundation.logging.Logger;
import com.aquima.plugin.dossier.config.DossierConfig;

import com.blueriq.component.api.datasource.DataSourceProfile;
import com.blueriq.component.api.datasource.DatasourcePropertiesUtil;
import com.blueriq.component.api.datasource.IDatasourceProperties;
import com.blueriq.component.api.datasource.IDatasourcePropertiesConfiguration;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Profile(DataSourceProfile.JNDI_DATASOURCE_PROFILE_NAME)
public class DossierJndiConfig {
  private static final Logger LOG = LogFactory.getLogger(DossierJndiConfig.class);

  @Autowired
  private ConfigurableEnvironment configurableEnv;

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
    sessionFactory.setHibernateProperties(
        DatasourcePropertiesUtil.getHibernateProperties(configurableEnv, DossierConfig.PLUGIN_NAME));
    return sessionFactory;
  }

  @Autowired
  @Bean(name = DossierConfig.DATA_SOURCE_NAME)
  public DataSource dossierDataSource(IDatasourcePropertiesConfiguration dsConfigProperties) {
    IDatasourceProperties properties = dsConfigProperties.getDatasource().get(DossierConfig.PLUGIN_NAME);
    return getJndiDataSource(properties.getJndiName());
  }

  private DataSource getJndiDataSource(String jndiName) {
    try {
      return new JndiDataSourceLookup().getDataSource(jndiName);
    } catch (Exception ex) {
      LOG.error(
          String.format("Unable to get the JNDI Data Source with name '%s'. Cause: %s.", jndiName, ex.getMessage()));
      throw new IllegalArgumentException(String.format("Unable to get the JNDI Data Source with name '%s'.", jndiName),
          ex);
    }
  }
}
