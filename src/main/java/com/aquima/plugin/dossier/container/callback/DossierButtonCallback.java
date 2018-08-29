package com.aquima.plugin.dossier.container.callback;

import com.aquima.interactions.composer.IElement;
import com.aquima.interactions.portal.ICallbackFunction;
import com.aquima.interactions.portal.IContainerEventContext;
import com.aquima.plugin.dossier.RequestKeys;

public class DossierButtonCallback implements ICallbackFunction {

  private static final long serialVersionUID = -4044522923493305416L;
  private final Long dossierId;

  public DossierButtonCallback(Long dossierId) {
    this.dossierId = dossierId;
  }

  @Override
  public void handle(IElement element, IContainerEventContext context) throws Exception {
    context.getRequestScope().setAttribute(RequestKeys.DOSSIER_ID, String.valueOf(dossierId));
  }
}
