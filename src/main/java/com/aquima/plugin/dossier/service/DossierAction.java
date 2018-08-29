package com.aquima.plugin.dossier.service;


import com.aquima.interactions.foundation.text.StringUtil;

import java.io.Serializable;

/**
 * Enumerated type class containing the possible dossier manager actions.
 * 
 * @author O. Kerpershoek
 * 
 * @since 7.0
 */
public final class DossierAction implements Serializable {

  protected static final DossierAction INIT_AUTOSET = new DossierAction("init-autoset");
  protected static final DossierAction INIT = new DossierAction("init");
  protected static final DossierAction LOAD = new DossierAction("load");
  protected static final DossierAction SAVE = new DossierAction("save");
  protected static final DossierAction DELETE = new DossierAction("delete");
  protected static final DossierAction UPDATE = new DossierAction("update");
  protected static final DossierAction[] ALL_ACTIONS = { INIT_AUTOSET, INIT, LOAD, SAVE, DELETE, UPDATE, };

  private final String mType;

  protected static DossierAction valueOf(String type) {
    if (StringUtil.isEmpty(type)) {
      return null;
    }

    for (DossierAction action : ALL_ACTIONS) {
      if (action.mType.equalsIgnoreCase(type)) {
        return action;
      }
    }

    throw new IllegalArgumentException("Invalid action type: " + type);
  }


  private DossierAction(String type) {
    if (StringUtil.isEmpty(type)) {
      throw new IllegalArgumentException("Invalid type name passed to DossierAction constructor");
    }

    this.mType = StringUtil.normalize(type);
  }

  @Override
  public int hashCode() {
    return this.mType.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DossierAction) {
      DossierAction other = (DossierAction) obj;

      return this.mType.equals(other.mType);
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer(32);

    buffer.append("[DossierAction ");
    buffer.append(this.mType);
    buffer.append(']');

    return buffer.toString();
  }
}
