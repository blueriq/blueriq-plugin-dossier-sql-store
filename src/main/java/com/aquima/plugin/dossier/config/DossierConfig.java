package com.aquima.plugin.dossier.config;

import com.blueriq.component.api.plugin.IPluginMetadata;
import com.blueriq.component.api.plugin.PluginMetadata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { //
    "com.aquima.plugin.dossier.config", //
    "com.aquima.plugin.dossier.data", //
    "com.aquima.plugin.dossier.repository", //
    "com.aquima.plugin.dossier.model", //
    "com.aquima.plugin.dossier.service", //
    "com.aquima.plugin.dossier.container", //
    "com.aquima.plugin.dossier.properties" })
public class DossierConfig {

  public static final String ENTITY_BASE_PACKAGE = "com.aquima.plugin.dossier.model";
  public static final String DATA_SOURCE_NAME = "dossierDataSource";
  public static final String SESSION_FACTORY_NAME = "dossierSessionFactory";
  public static final String TRANSACTION_MANAGER_NAME = "dossierTransactionManager";
  public static final String PLUGIN_NAME = "dossier";
}
