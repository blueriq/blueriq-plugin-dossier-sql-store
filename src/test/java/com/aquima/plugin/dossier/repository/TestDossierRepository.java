package com.aquima.plugin.dossier.repository;

import com.aquima.interactions.foundation.logging.LogFactory;
import com.aquima.interactions.foundation.logging.Logger;
import com.aquima.plugin.dossier.model.Dossier;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestDossierRepository implements IDossierRepository {

  private static final Logger LOG = LogFactory.getLogger(TestDossierRepository.class);

  public static final String EVENT_FINDALL = "findAll";
  public static final String EVENT_FINDBYFEATURE = "findByFeature";
  public static final String EVENT_LOAD = "load";
  public static final String EVENT_SAVE = "save";
  public static final String EVENT_DELETE = "delete";

  private String mSavedProfileXml;
  private final List<String> mEvents = new ArrayList<String>();
  private Dossier mSavedDossier;

  @Override
  public List<Dossier> findByType(String dossierType) {
    List<Dossier> result = new ArrayList<Dossier>();
    result.add(createDossier(Long.valueOf(1), "feature1", "feature2"));
    result.add(createDossier(Long.valueOf(2), "balat", "gdfhjgfjdkg"));
    this.mEvents.add(EVENT_FINDALL);
    return result;
  }

  public void assertOnlyEvent(String event) {
    if (this.mEvents.size() != 1 || !this.mEvents.contains(event)) {
      Assert.fail("TestDao must have called " + event + ", events called: " + this.mEvents);
    }
  }

  public void assertEvent(String event) {
    if (!this.mEvents.contains(event)) {
      Assert.fail("TestDao must have called " + event + ", events called: " + this.mEvents);
    }
  }

  public void assertNrOfEvents(int nrOfEvents) {
    if (this.mEvents.size() != nrOfEvents) {
      Assert.fail("TestDao must have called " + nrOfEvents + " events, events called: " + this.mEvents);
    }
  }

  public Dossier getSavedDossier() {
    return this.mSavedDossier;
  }

  private static Dossier createDossier(Long id, String feature1, String feature2) {
    Dossier dossier = new Dossier();
    dossier.setFeature1(feature1);
    dossier.setFeature2(feature2);
    dossier.setId(id);
    dossier.setType("dossierType");
    return dossier;
  }

  @Override
  public void delete(Dossier dossier) {
    Assert.assertNotNull("unable to delete emtpy dossier", dossier);
    LOG.info("dossier delete with id: " + dossier);
    this.mEvents.add(EVENT_DELETE);
  }

  @Override
  public Dossier findOne(Long id) {
    Assert.assertNotNull("unable to select with emtpy id", id);
    Dossier d = createDossier(id, "feature1", "feature2");
    d.setProfileXml(this.mSavedProfileXml);
    this.mEvents.add(EVENT_LOAD);
    return d;
  }

  @Override
  public Dossier save(Dossier d) {
    Assert.assertNotNull("unable to insert emtpy dossier");
    LOG.info("dossier inserted: " + d.getProfileXml());
    this.mSavedProfileXml = d.getProfileXml();
    d.setId(Long.valueOf(1));
    this.mEvents.add(EVENT_SAVE);
    this.mSavedDossier = d;
    return d;
  }

  @Override
  public List<Dossier> findAll() {
    ArrayList<Dossier> result = new ArrayList<Dossier>();
    result.add(createDossier(Long.valueOf(1), "feature1", "feature2"));
    result.add(createDossier(Long.valueOf(2), "balat", "gdfhjgfjdkg"));
    this.mEvents.add(EVENT_FINDBYFEATURE);
    return result;
  }

  @Override
  public List<Dossier> find(String dossierType, Map<String, Object> searchValues) {
    return null;
  }

  @Override
  public void delete(Long id) {
    Assert.assertNotNull("unable to delete emtpy dossier", id);
    LOG.info("dossier delete with id: " + id);
    this.mEvents.add(EVENT_DELETE);
  }

}
