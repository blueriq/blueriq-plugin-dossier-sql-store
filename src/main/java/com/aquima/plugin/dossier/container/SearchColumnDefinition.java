package com.aquima.plugin.dossier.container;

 
import com.aquima.interactions.foundation.text.StringUtil;
import com.aquima.interactions.metamodel.AttributeReference;

/**
 * This class contains a single column definition that should be used to search on.
 * 
 * @author O. Kerpershoek
 * 
 * @since 7.0
 */
public class SearchColumnDefinition {
  private final String mFeature;
  private final boolean mIsDateFeature;
  private final AttributeReference mAttribute;

  protected SearchColumnDefinition(String feature, boolean isDateFeature, AttributeReference reference) {
    if (StringUtil.isEmpty(feature)) {
      throw new IllegalArgumentException("Invalid (empty) feature name passed to search column definition.");
    }
    if (reference == null) {
      throw new IllegalArgumentException("Invalid (null) attribute reference passed to search column definition.");
    }

    this.mFeature = feature;
    this.mIsDateFeature = isDateFeature;
    this.mAttribute = reference;
  }

  protected AttributeReference getAttribute() {
    return this.mAttribute;
  }

  protected boolean isDateColumn() {
    return this.mIsDateFeature;
  }

  protected String getFeature() {
    return this.mFeature;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer(64);

    buffer.append("[SearchColumn name=") //
        .append(this.mFeature) //
        .append(" attribute=") //
        .append(this.mAttribute) //
        .append(']');

    return buffer.toString();
  }
}
