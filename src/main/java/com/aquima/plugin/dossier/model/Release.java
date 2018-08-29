package com.aquima.plugin.dossier.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "dossier_releases")
public class Release {

  // Table name
  public static final String TABLE = "dossier_releases";

  // Column names
  public static final String ID = "id";
  public static final String DESCRIPTION = "description";
  public static final String RELEASEDATE = "releasedate";
  public static final String VERSION = "version";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SG_dossier_releases")
  @SequenceGenerator(name = "SG_dossier_releases", sequenceName = "s_dossier_releaseid", initialValue = 1,
      allocationSize = 1)
  @Column(name = ID)
  private long id;

  @Column(name = DESCRIPTION, length = 150)
  private String description;

  @Column(name = RELEASEDATE, nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date releaseDate;

  @Column(name = VERSION, nullable = false, length = 100)
  private String version;
}
