package com.aquima.plugin.dossier.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "blueriq.dossier.list")
public class DossierListProperties {
  private int pagingsize = 20;

  public int getPagingsize() {
    return pagingsize;
  }

  public void setPagingsize(int pagingsize) {
    this.pagingsize = pagingsize;
  }

}
