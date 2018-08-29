package com.aquima.plugin.dossier.service;

import com.aquima.interactions.foundation.IParameters;
import com.aquima.interactions.foundation.exception.AppException;
 
import com.aquima.interactions.foundation.text.StringUtil;
import com.aquima.interactions.metamodel.AttributeReference;
import com.aquima.interactions.metamodel.IEntityDefinition;
import com.aquima.interactions.metamodel.IMetaModel;
import com.aquima.plugin.dossier.ParameterKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is responsible for parsing and validating the dossier manager parameters.
 * 
 * @author O. Kerpershoek
 * 
 * @since 7.0
 */
public class DossierManagerParameters {
  private final DossierAction mAction;
  private final List<String> mUndeletables;
  private final List<String> mTransients;
  private final AttributeReference mIdAttribute;
  private final String mDossierType;

  protected DossierManagerParameters(IParameters parameters, IMetaModel model) throws AppException {
    this.mAction = this.parseDossierAction(parameters);
    this.mUndeletables = this.parseUndeletableInstances(parameters, model);
    this.mTransients = this.parseTransientInstances(parameters, model);
    this.mIdAttribute = this.parseIdAttribute(parameters, model);
    this.mDossierType = this.parseDossierType(parameters);

  }

  private DossierAction parseDossierAction(IParameters parameters) throws AppException {
    DossierAction result = DossierAction.valueOf(parameters.getParameter(ParameterKeys.DossierManager.ACTION));

    if (result == null) {
      throw new AppException("Verplichte parameter niet gespecificeerd.[" + ParameterKeys.DossierManager.ACTION + "]");
    }

    return result;
  }

  protected DossierAction getAction() {
    return this.mAction;
  }

  private List<String> parseUndeletableInstances(IParameters parameters, IMetaModel model) throws AppException {
    List<String> result = new ArrayList<String>(8);

    String paramUndeletables = parameters.getParameter(ParameterKeys.DossierManager.UNDELETABLE_INSTANCES);

    if (StringUtil.notEmpty(paramUndeletables)) {
      StringTokenizer tok = new StringTokenizer(paramUndeletables, "|");

      while (tok != null && tok.hasMoreTokens()) {
        String entityName = tok.nextToken();

        // make sure the entity exists
        if (!model.containsEntity(entityName)) {
          throw new AppException("The parameter '" + ParameterKeys.DossierManager.UNDELETABLE_INSTANCES
              + "' contains an unknown entity name: " + entityName);
        }

        result.add(StringUtil.normalize(entityName));
      }
    }

    result.add("system");

    return result;
  }

  protected boolean shouldDeletableInstances(String entityName) {
    if (StringUtil.isEmpty(entityName)) {
      throw new IllegalArgumentException("Invalid (null) entity name passed to 'shouldDeleteInstance' method.");
    }

    String key = StringUtil.normalize(entityName);

    return this.mUndeletables.contains(key);
  }

  private List<String> parseTransientInstances(IParameters parameters, IMetaModel model) throws AppException {
    List<String> result = new ArrayList<String>(16);

    String teValue = parameters.getParameter(ParameterKeys.DossierManager.TRANSIENT_ENTITIES);

    if (StringUtil.notEmpty(teValue)) {
      final StringTokenizer tok = new StringTokenizer(teValue.trim(), "|");

      while (tok.hasMoreTokens()) {
        String entityName = tok.nextToken();

        // make sure the entity exists
        if (!model.containsEntity(entityName)) {
          throw new AppException("The parameter '" + ParameterKeys.DossierManager.TRANSIENT_ENTITIES
              + "' contains an unknown entity name: " + entityName);
        }

        result.add(StringUtil.normalize(entityName));
      }
    }

    result.add("system");

    return result;
  }

  protected boolean isTransientEntity(String entityName) {
    if (StringUtil.isEmpty(entityName)) {
      throw new IllegalArgumentException("Invalid (null) entity name passed to 'isTransientEntity' method.");
    }

    String key = StringUtil.normalize(entityName);

    return this.mTransients.contains(key);
  }

  private AttributeReference parseIdAttribute(IParameters parameters, IMetaModel model) throws AppException {
    String dossierIdAttr = parameters.getParameter(ParameterKeys.DossierManager.DOSSIER_ID_ATTRIBUTE);

    // AQ-308: trim all spaces
    dossierIdAttr = dossierIdAttr.trim();

    if (dossierIdAttr == null) {
      if (this.mAction.equals(DossierAction.LOAD) || this.mAction.equals(DossierAction.SAVE)
          || this.mAction.equals(DossierAction.UPDATE)) {

        // dossier ID attribute is required for certain actions.
        throw new AppException("Required parameter '" + ParameterKeys.DossierManager.DOSSIER_ID_ATTRIBUTE
            + "' not specified, this parameter is required for action: " + this.mAction);
      } else {
        return null;
      }
    }

    AttributeReference reference = new AttributeReference(dossierIdAttr);

    // make sure the ID attribute belongs to a singleton.
    IEntityDefinition entity = model.getEntityDefinition(reference.getEntityName());

    if (!entity.isSingleton()) {
      throw new AppException("Invalid parameter value for '" + ParameterKeys.DossierManager.DOSSIER_ID_ATTRIBUTE
          + "', the attribute '" + dossierIdAttr + "' should belong to a singleton.");
    }

    // make sure the attribute actually exists
    if (!entity.containsAttribute(reference.getAttributeName(), true)) {
      throw new AppException("Invalid parameter value for '" + ParameterKeys.DossierManager.DOSSIER_ID_ATTRIBUTE
          + "', the attribute '" + dossierIdAttr + "' could not be found.");
    }

    return reference;
  }

  protected AttributeReference getIdAttribute() {
    return this.mIdAttribute;
  }

  private String parseDossierType(IParameters parameters) {
    String type = parameters.getParameter(ParameterKeys.DossierManager.DOSSIERTYPE);
    if (StringUtil.isEmpty(type)) {
      throw new AppException("Required parameter '" + ParameterKeys.DossierManager.DOSSIERTYPE
          + "' missing. Please add the dossier type parameter to the DossierManager container parameters.");
    }
    return type;
  }

  public String getDossierType() {
    return this.mDossierType;
  }

}
