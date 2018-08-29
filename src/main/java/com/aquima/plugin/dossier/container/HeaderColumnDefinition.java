package com.aquima.plugin.dossier.container;

import com.aquima.interactions.foundation.text.IMultilingualText;
import com.aquima.interactions.foundation.text.StringUtil;
import com.aquima.interactions.metamodel.IDomainDefinition;

/**
 * This class contains the definition of a single header for the dossier list. The header definition is parsed from the
 * parameters passed to the dossier list container.
 * 
 * @author O. Kerpershoek
 * 
 * @since 7.0
 */
public class HeaderColumnDefinition {
  private static final String DATE = "date";
  private static final String FEATURE = "feature";

  private final String feature;
  private final IMultilingualText displayText;
  private final IDomainDefinition domain;

  protected HeaderColumnDefinition(String featureName, IMultilingualText message, IDomainDefinition domain) {
    if (StringUtil.isEmpty(featureName)) {
      throw new IllegalArgumentException("Invalid (empty) feature name passed to HeaderColumnDefinition constructor");
    }

    this.feature = StringUtil.normalize(featureName);
    this.displayText = message;
    this.domain = domain;
  }

  protected String getType() {
    if (this.feature.startsWith(DATE)) {
      return DATE;
    } else {
      return FEATURE;
    }
  }

  protected IMultilingualText getMessage() {
    return this.displayText;
  }

  protected int getIndex() {
    try {
      return Integer.parseInt(this.feature.substring(this.feature.length() - 1));
    } catch (NumberFormatException error) {
      return -1;
    }
  }

  protected IDomainDefinition getDomain() {
    return this.domain;
  }

  protected String getFeature() {
    return this.feature;
  }
}
