package com.aquima.plugin.dossier.container;

import com.aquima.interactions.foundation.IParameters;
import com.aquima.interactions.foundation.exception.AppException;
import com.aquima.interactions.foundation.logging.LogFactory;
import com.aquima.interactions.foundation.logging.Logger;
import com.aquima.interactions.foundation.text.IMultilingualText;
import com.aquima.interactions.foundation.text.MultilingualText;
import com.aquima.interactions.foundation.text.StringUtil;
import com.aquima.interactions.metamodel.AttributeReference;
import com.aquima.interactions.metamodel.IDomainDefinition;
import com.aquima.interactions.metamodel.IMetaModel;
import com.aquima.interactions.metamodel.exception.UnknownMessageException;
import com.aquima.interactions.portal.IContainerContext;
import com.aquima.interactions.project.IProject;
import com.aquima.plugin.dossier.ParameterKeys;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing the parameters of the dossier list container.
 * 
 * @author O. Kerpershoek
 * 
 * @since 7.0
 */
public class DossierListParameters {

  private static final Logger LOG = LogFactory.getLogger(DossierListParameters.class);

  private final String mEmptyContainer;
  private final List<SearchColumnDefinition> mSearchColumns;
  private final List<HeaderColumnDefinition> mHeaderColumns;
  private final String mDossierType;

  protected DossierListParameters(IContainerContext context) throws AppException {
    IParameters parameters = context.getParameters();
    IMetaModel model = context.getMetaModel();

    this.mEmptyContainer = this.parseEmptyContainer(parameters);
    this.mSearchColumns = this.parseSearchColumns(parameters);
    this.mHeaderColumns = this.parseHeaderColumns(parameters, model, context.getProject());
    this.mDossierType = this.parseDossierType(parameters);
  }

  private String parseEmptyContainer(IParameters parameters) {
    String emptyContainer = parameters.getParameter(ParameterKeys.DossierList.NO_RESULT_CONTAINER);

    if (StringUtil.isEmpty(emptyContainer) && LOG.isDebugEnabled()) {
      LOG.debug("No parameter " + ParameterKeys.DossierList.NO_RESULT_CONTAINER
          + " defined, the dossier list will be returned even when empty.");
    }

    return emptyContainer;
  }

  protected String getEmptyContainer() {
    return this.mEmptyContainer;
  }

  private List<SearchColumnDefinition> parseSearchColumns(IParameters parameters) {
    List<SearchColumnDefinition> searchColumns = new ArrayList<SearchColumnDefinition>(16);

    for (String feature : ParameterKeys.DossierList.ALL_FEATURES) {
      String attrName = parameters.getParameter(feature);

      if (StringUtil.isEmpty(attrName)) {
        continue;
      }

      AttributeReference reference = new AttributeReference(attrName);

      searchColumns.add(new SearchColumnDefinition(feature, false, reference));
    }
    for (String feature : ParameterKeys.DossierList.ALL_DATES) {
      String attrName = parameters.getParameter(feature);

      if (StringUtil.isEmpty(attrName)) {
        continue;
      }

      AttributeReference reference = new AttributeReference(attrName);

      searchColumns.add(new SearchColumnDefinition(feature, true, reference));
    }

    return searchColumns;
  }

  protected SearchColumnDefinition[] getSearchColumns() {
    SearchColumnDefinition[] result = new SearchColumnDefinition[this.mSearchColumns.size()];

    this.mSearchColumns.toArray(result);

    return result;
  }

  private List<HeaderColumnDefinition> parseHeaderColumns(IParameters parameters, IMetaModel model, IProject project)
      throws AppException {
    List<HeaderColumnDefinition> headerColumns = new ArrayList<HeaderColumnDefinition>(8);

    final String pval = parameters.getParameter(ParameterKeys.DossierList.HEADERS);
    if (StringUtil.isEmpty(pval)) {
      throw new AppException("Required parameter '" + ParameterKeys.DossierList.HEADERS
          + "' missing. Please add a comma seperated list of header definitions for this parameter.");
    }
    final String[] hs = StringUtil.split(pval, ",");

    for (String headerStr : hs) {
      int separatorIdx = headerStr.indexOf('=');

      if (separatorIdx <= 0) {
        throw new AppException("Invalid header definition '" + headerStr + "', the header definition for parameter '"
            + ParameterKeys.DossierList.HEADERS + "' should be in the format '<feature>=<domain@message>'.");
      }

      String featureName = headerStr.substring(0, separatorIdx);
      String messageStr = headerStr.substring(separatorIdx + 1);
      IDomainDefinition domain = null;

      // message may be preceded by the name of the domain for the column.
      int domainIdx = messageStr.indexOf('@');

      if (domainIdx > 0) {
        String domainName = messageStr.substring(0, domainIdx);
        messageStr = messageStr.substring(domainIdx + 1);

        domain = model.getDomainDefinition(domainName);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[parseHeaderColumns] The domain '" + domain.getName()
              + "' will be used for the display values of column: " + featureName);
        }
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("[parseHeaderColumns] No domain defined for header '" + featureName
              + "'. The values in this column will be shown 'as-is'.");
        }
      }

      IMultilingualText message;

      try {
        message = project.getMessage(messageStr);
      } catch (UnknownMessageException notDefined) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("[parseHeaderColumns] No message defined for '" + messageStr
              + "', the value will be used as header column.");
        }

        message = new MultilingualText(messageStr);
      }

      headerColumns.add(new HeaderColumnDefinition(featureName, message, domain));
    }

    return headerColumns;
  }

  protected HeaderColumnDefinition[] getHeaderColumns() {
    HeaderColumnDefinition[] result = new HeaderColumnDefinition[this.mHeaderColumns.size()];

    this.mHeaderColumns.toArray(result);

    return result;
  }

  private String parseDossierType(IParameters parameters) {
    String type = parameters.getParameter(ParameterKeys.DossierList.DOSSIERTYPE);
    if (StringUtil.isEmpty(type)) {
      throw new AppException("Required parameter '" + ParameterKeys.DossierList.DOSSIERTYPE
          + "' missing. Please add the dossier type parameter to the DossierList container parameters.");
    }
    return type;
  }

  public String getDossierType() {
    return this.mDossierType;
  }

}
