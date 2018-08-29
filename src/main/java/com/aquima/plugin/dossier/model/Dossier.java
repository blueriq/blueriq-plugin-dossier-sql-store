package com.aquima.plugin.dossier.model;

import com.aquima.interactions.foundation.IPrimitiveValue;
import com.aquima.interactions.foundation.exception.InvalidStateException;
import com.aquima.interactions.foundation.types.DateValue;
import com.aquima.interactions.foundation.types.StringValue;
import com.aquima.plugin.dossier.ParameterKeys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = Dossier.TABLE)
public class Dossier {

  // Table name
  public static final String TABLE = "aq_dossier";

  // Column names
  public static final String ID = "id";
  public static final String DATE1 = "date1";
  public static final String DATE2 = "date2";
  public static final String FEATURE1 = "feature1";
  public static final String FEATURE2 = "feature2";
  public static final String FEATURE3 = "feature3";
  public static final String FEATURE4 = "feature4";
  public static final String PROFILEXML = "profilexml";
  public static final String TYPE = "type";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DossierSequenceGenerator")
  @SequenceGenerator(name = "DossierSequenceGenerator", sequenceName = "hibernate_sequence", initialValue = 1,
      allocationSize = 1)
  @Column(name = ID)
  private Long id;

  @Column(name = DATE1)
  @Temporal(TemporalType.TIMESTAMP)
  private Date date1;

  @Column(name = DATE2)
  @Temporal(TemporalType.TIMESTAMP)
  private Date date2;

  @Column(name = FEATURE1, length = 50)
  private String feature1;

  @Column(name = FEATURE2, length = 50)
  private String feature2;

  @Column(name = FEATURE3, length = 50)
  private String feature3;

  @Column(name = FEATURE4, length = 50)
  private String feature4;

  @Lob
  @Column(name = PROFILEXML, nullable = false)
  private String profileXml;

  @Column(name = TYPE, length = 255)
  private String type;

  public Dossier() {
    // default constructor for hibernate
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFeature1() {
    return this.feature1;
  }

  public StringValue getFeature1AsIValue() {
    return new StringValue(this.feature1);
  }

  public void setFeature1(final String feature1) {
    this.feature1 = feature1;
  }

  public void setFeature1(final StringValue feature1) {
    if (feature1.isUnknown()) {
      this.feature1 = null;
    } else {
      this.feature1 = feature1.stringValue();
    }
  }

  public String getFeature2() {
    return this.feature2;
  }

  public StringValue getFeature2AsIValue() {
    return new StringValue(this.feature2);
  }

  public void setFeature2(final String feature2) {
    this.feature2 = feature2;
  }

  public void setFeature2(final StringValue feature2) {
    if (feature2.isUnknown()) {
      this.feature2 = null;
    } else {
      this.feature2 = feature2.stringValue();
    }
  }

  public String getFeature3() {
    return this.feature3;
  }

  public StringValue getFeature3AsIValue() {
    return new StringValue(this.feature3);
  }

  public void setFeature3(final String feature3) {
    this.feature3 = feature3;
  }

  public void setFeature3(final StringValue feature3) {
    if (feature3.isUnknown()) {
      this.feature3 = null;
    } else {
      this.feature3 = feature3.stringValue();
    }
  }

  public String getFeature4() {
    return this.feature4;
  }

  public StringValue getFeature4AsIValue() {
    return new StringValue(this.feature4);
  }

  public void setFeature4(final String feature4) {
    this.feature4 = feature4;
  }

  public void setFeature4(final StringValue feature4) {
    if (feature4.isUnknown()) {
      this.feature4 = null;
    } else {
      this.feature4 = feature4.stringValue();
    }
  }

  public String getProfileXml() {
    return this.profileXml;
  }

  public void setProfileXml(final String profileXml) {
    this.profileXml = profileXml;
  }

  public Date getDate1() {
    return this.date1;
  }

  public DateValue getDate1AsIValue() {
    return new DateValue(this.date1);
  }

  public void setDate1(Date date1) {
    this.date1 = date1;
  }

  public void setDate1(final DateValue date1) {
    if (date1.isUnknown()) {
      this.date1 = null;
    } else {
      this.date1 = date1.dateValue();
    }
  }

  public Date getDate2() {
    return this.date2;
  }

  public DateValue getDate2AsIValue() {
    return new DateValue(this.date2);
  }

  public void setDate2(Date date2) {
    this.date2 = date2;
  }

  public void setDate2(final DateValue date2) {
    if (date2.isUnknown()) {
      this.date2 = null;
    } else {
      this.date2 = date2.dateValue();
    }
  }

  public IPrimitiveValue getValue(String feature) {
    if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_1)) {
      return this.getFeature1AsIValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_2)) {
      return this.getFeature2AsIValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_3)) {
      return this.getFeature3AsIValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_4)) {
      return this.getFeature4AsIValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.DATE_1)) {
      return this.getDate1AsIValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.DATE_2)) {
      return this.getDate2AsIValue();
    } else {
      throw new InvalidStateException("Invalid feature label: " + feature);
    }
  }

  public void setValue(String feature, IPrimitiveValue attrValue) {
    if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_1)) {
      this.feature1 = ((StringValue) attrValue).stringValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_2)) {
      this.feature2 = ((StringValue) attrValue).stringValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_3)) {
      this.feature3 = ((StringValue) attrValue).stringValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.FEATURE_4)) {
      this.feature4 = ((StringValue) attrValue).stringValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.DATE_1)) {
      this.date1 = ((DateValue) attrValue).dateValue();
    } else if (feature.equalsIgnoreCase(ParameterKeys.DossierList.DATE_2)) {
      this.date2 = ((DateValue) attrValue).dateValue();
    } else {
      throw new InvalidStateException("Invalid feature label: " + feature);
    }
  }
}
