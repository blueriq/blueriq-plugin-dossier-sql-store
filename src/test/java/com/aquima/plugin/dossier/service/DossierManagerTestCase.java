package com.aquima.plugin.dossier.service;

import com.aquima.interactions.composer.IPage;
import com.aquima.interactions.foundation.DataType;
import com.aquima.interactions.foundation.Parameters;
import com.aquima.interactions.foundation.exception.AppException;
import com.aquima.interactions.foundation.text.MultilingualText;
import com.aquima.interactions.foundation.types.DateValue;
import com.aquima.interactions.foundation.types.IntegerValue;
import com.aquima.interactions.framework.service.ServiceResult;
import com.aquima.interactions.portal.IService;
import com.aquima.interactions.portal.IServiceContext;
import com.aquima.interactions.portal.IServiceResult;
import com.aquima.interactions.portal.ServiceException;
import com.aquima.interactions.profile.IEntityInstance;
import com.aquima.interactions.profile.IProfile;
import com.aquima.interactions.test.templates.ApplicationTemplate;
import com.aquima.interactions.test.templates.model.EntityTemplate;
import com.aquima.interactions.test.templates.session.PortalSessionTestFacade;
import com.aquima.interactions.test.templates.session.RequestTemplate;
import com.aquima.plugin.dossier.ParameterKeys;
import com.aquima.plugin.dossier.repository.TestDossierRepository;

import junit.framework.TestCase;

public class DossierManagerTestCase extends TestCase {

  private PortalSessionTestFacade session;
  private TestDossierRepository testRepository;

  private IService dossierManager;

  @Override
  public void setUp() throws Exception {
    testRepository = new TestDossierRepository();
    dossierManager = new DossierManager(testRepository);
  }

  @Override
  public void tearDown() {
    testRepository = null;
    if (session != null) {
      session.close();
    }
    session = null;
  }

  public void testDossierInit() throws Exception {
    Parameters params = createParams("init");
    params.setParameter("undeletable_instances", "Address");
    session = initializePortalSession(params, new IService() {

      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        DossierManagerTestCase.this.initProfileService().handle(context);
        // create an instance of Person and Address
        IProfile profile = context.getProfile();
        profile.createInstance("Person");
        profile.createInstance("Address");
        return new ServiceResult();
      }
    });

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());

    // press ok:
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(0);

    assertEquals("Person must be deleted", 0, session.getProfile().getAllInstancesForEntity("Person", true).length);
    assertEquals("Address must NOT be deleted", 1,
        session.getProfile().getAllInstancesForEntity("Address", true).length);
  }

  public void testDossierLoad() throws Exception {
    session = initializePortalSession(createParams("load"), null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));
    assertNotNull(session.getCurrentPage());
    testRepository.assertOnlyEvent(TestDossierRepository.EVENT_LOAD);
    assertDossier(session.getProfile(), 1);
  }

  public void testDossierLoadUsingProfile() throws Exception {
    session = initializePortalSession(createParams("load"), initDossierIdService(1));

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", new RequestTemplate());
    assertNotNull(session.getCurrentPage());
    testRepository.assertOnlyEvent(TestDossierRepository.EVENT_LOAD);
    assertDossier(session.getProfile(), 1);
  }

  public void testDossierLoadNoSelection() throws Exception {
    session = initializePortalSession(createParams("load"), null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select NO dossier and press ok:
    session.handleButtonEvent("ok", null);
    IPage page = session.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("Error expected", 1, page.getGenericErrors().length);
    assertEquals("Ivalid error message", new MultilingualText("service.dossiermanager.error.no.dossier.selected"),
        page.getGenericErrors()[0].getText());
    testRepository.assertNrOfEvents(0);
  }

  public void testDossierSave() throws Exception {
    session = initializePortalSession(createParams("save"), null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // press ok:
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(3); // (two times save)
    assertDossier(session.getProfile(), 1);
    assertNotNull("no dossier model saved", testRepository.getSavedDossier());
    assertNotNull("missing id in dossier model", testRepository.getSavedDossier().getId());
    assertEquals("id in dossier model", Long.valueOf(1), testRepository.getSavedDossier().getId());
    assertEquals("feature1 in dossier model", "test", testRepository.getSavedDossier().getFeature1());
    assertEquals("feature2 in dossier model", "Jon", testRepository.getSavedDossier().getFeature2());
    assertEquals("feature3 in dossier model", "test", testRepository.getSavedDossier().getFeature3());
    assertEquals("feature4 in dossier model", "1", testRepository.getSavedDossier().getFeature4());
    assertEquals("date1 in dossier model", DateValue.createToday(),
        testRepository.getSavedDossier().getDate1AsIValue());
    assertEquals("date2 in dossier model", DateValue.createToday(),
        testRepository.getSavedDossier().getDate2AsIValue());
  }

  public void testDossierUpdate() throws Exception {
    session = initializePortalSession(createParams("update"), new IService() {

      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        DossierManagerTestCase.this.initProfileService().handle(context);
        IEntityInstance dossier = context.getProfile().getSingletonInstance("Dossier", true);
        dossier.setValue("Identifier", Integer.valueOf(1));
        return new ServiceResult();
      }
    });

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // press ok:
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    testRepository.assertEvent(TestDossierRepository.EVENT_LOAD);
    testRepository.assertEvent(TestDossierRepository.EVENT_SAVE);
    testRepository.assertNrOfEvents(2);
  }

  public void testDossierUpdateNoId() throws Exception {
    session = initializePortalSession(createParams("update"), null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // press ok:
    session.handleButtonEvent("ok", null);
    IPage page = session.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("Error expected", 1, page.getGenericErrors().length);
    assertEquals("Ivalid error message", new MultilingualText("service.dossiermanager.error.no.dossier.selected"),
        page.getGenericErrors()[0].getText());
    testRepository.assertNrOfEvents(0);
  }

  public void testDossierDelete() throws Exception {
    session = initializePortalSession(createParams("delete"), null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(2); // load and delete
  }

  public void testDossierDeleteUsingProfile() throws Exception {
    session = initializePortalSession(createParams("delete"), initDossierIdService(1));

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", new RequestTemplate());
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(2); // load and delete
  }

  public void testDossierDeleteNoSelection() throws Exception {
    session = initializePortalSession(createParams("delete"), null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select NO dossier and press ok:
    session.handleButtonEvent("ok", null);
    IPage page = session.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("Error expected", 1, page.getGenericErrors().length);
    assertEquals("Ivalid error message", new MultilingualText("service.dossiermanager.error.no.dossier.selected"),
        page.getGenericErrors()[0].getText());
  }

  public void testSaveInitLoadUpdate() throws Exception {
    // ///// save
    session = initializePortalSession(createParams("save"), new IService() {

      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        DossierManagerTestCase.this.initProfileService();
        // create an instance of Person and Address
        IProfile profile = context.getProfile();
        profile.createInstance("Person");
        profile.createInstance("Person");
        profile.createInstance("Address");
        return new ServiceResult();
      }
    });

    // start flow:
    session.startFlow("save_init_load");
    assertNotNull(session.getCurrentPage());
    // press ok:
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(1);
    testRepository.assertEvent(TestDossierRepository.EVENT_SAVE);

    // ///// init
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    assertEquals("No Person must exist", 0, session.getProfile().getAllInstancesForEntity("Person", true).length);
    assertEquals("No Address must exist", 0, session.getProfile().getAllInstancesForEntity("Address", true).length);
    testRepository.assertNrOfEvents(1);

    // ///// load
    // press ok:
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(2);
    testRepository.assertEvent(TestDossierRepository.EVENT_LOAD);

    assertEquals("Person must be loaded", 2, session.getProfile().getAllInstancesForEntity("Person", true).length);
    assertEquals("Address must NOT be loaded (is set in 'transient_entities' parameter with save) ", 0,
        session.getProfile().getAllInstancesForEntity("Address", true).length);

    // ///// update
    // press ok:
    // select a dossier and press ok:
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(4);
    testRepository.assertEvent(TestDossierRepository.EVENT_SAVE);

    assertDossier(session.getProfile(), 1);
  }

  /**
   * Test case to reproduce issue: AQU-1729: Worklist Container & Services - Service stores only dynamic instances.
   * 
   * @throws Exception When something fails.
   */
  public void testSaveLoadWithStaticInstance() throws Exception {
    // save
    session = initializePortalSession(createParams("save"), new IService() {

      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        DossierManagerTestCase.this.initProfileService();
        // create an instance of Person and link with a static instance
        IEntityInstance person = context.getProfile().createInstance("Person");
        person.setValue("Bijlage", context.getProfile().getInstanceByName("Bijlage", "rijbewijs"));
        return new ServiceResult();
      }
    });

    // start flow:
    session.startFlow("save_init_load");
    assertNotNull(session.getCurrentPage());
    // press ok:
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(1);
    testRepository.assertEvent(TestDossierRepository.EVENT_SAVE);

    // ///// init
    session.handleButtonEvent("ok", null);
    assertNotNull(session.getCurrentPage());
    assertEquals("No Person must exist", 0, session.getProfile().getAllInstancesForEntity("Person", true).length);
    assertEquals("No Address must exist", 0, session.getProfile().getAllInstancesForEntity("Address", true).length);
    testRepository.assertNrOfEvents(1);

    // ///// load
    // press ok:
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));
    assertNotNull(session.getCurrentPage());
    testRepository.assertNrOfEvents(2);
    testRepository.assertEvent(TestDossierRepository.EVENT_LOAD);

    assertEquals("Person must be loaded", 1, session.getProfile().getAllInstancesForEntity("Person", true).length);
    assertEquals("Bijlage must still exist", 1, session.getProfile().getAllInstancesForEntity("Bijlage", true).length);
    IEntityInstance person = session.getProfile().getAllInstancesForEntity("Person", true)[0];
    IEntityInstance bijlage = session.getProfile().getInstanceByName("Bijlage", "rijbewijs");
    assertEquals("Person must be linked with the static instance", bijlage.getInstanceReference(),
        person.getValue("Bijlage"));
  }

  public void testNoDossierType() throws Exception {
    Parameters params = createParams("load");
    params.removeParameter("dossierType");

    session = initializePortalSession(params, null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));

    IPage page = session.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("Error expected", 1, page.getGenericErrors().length);
    assertEquals("Ivalid error message",
        new MultilingualText("Required parameter '" + ParameterKeys.DossierManager.DOSSIERTYPE
            + "' missing. Please add the dossier type parameter to the DossierManager container parameters."),
        page.getGenericErrors()[0].getText());
  }

  public void testOtherDossierType() throws Exception {
    Parameters params = createParams("load");
    params.setParameter("dossierType", "otherType");

    session = initializePortalSession(params, null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));

    IPage page = session.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("Error expected", 1, page.getGenericErrors().length);
    assertEquals("Ivalid error message", new MultilingualText("service.dossiermanager.error.no.dossier.selected"),
        page.getGenericErrors()[0].getText());
  }


  public void testAQ308SpaceDossierIDAttribute() throws Exception {

    Parameters parameters = createParams("load");

    parameters.setParameter("dossierIdAttribute", "    Dossier.Identifier    ");

    session = initializePortalSession(parameters, null);

    // start flow:
    session.startFlow("start");
    assertNotNull(session.getCurrentPage());
    // select a dossier and press ok:
    session.handleButtonEvent("ok", createEventWithSelection(1));
    assertNotNull(session.getCurrentPage());
    testRepository.assertOnlyEvent(TestDossierRepository.EVENT_LOAD);
    assertDossier(session.getProfile(), 1);
  }



  private void assertDossier(IProfile profile, int id) throws Exception {
    IEntityInstance dossier = profile.getSingletonInstance("Dossier", false);
    assertNotNull("Dossier instance", dossier);
    assertFalse("Dossier.Identifier", dossier.getValue("Identifier").isUnknown());
    assertEquals("Dossier.Identifier", id, dossier.getValue("Identifier").integerValue());
  }

  private RequestTemplate createEventWithSelection(int selectedDossierId) {
    RequestTemplate request = new RequestTemplate();
    request.addRequestValue("dossierId", String.valueOf(selectedDossierId));
    return request;
  }

  private static Parameters createParams(String action) {
    Parameters parameters = new Parameters(true);
    parameters.setParameter("dossierType", "dossierType");
    parameters.setParameter("dossierIdAttribute", "Dossier.Identifier");
    parameters.setParameter("action", action);
    parameters.setParameter("feature1", "Dossier.Name");
    parameters.setParameter("feature2", "Dossier.Person.Name");
    parameters.setParameter("feature3", "Dossier.Name");
    parameters.setParameter("feature4", "Dossier.Identifier");
    parameters.setParameter("date1", "Dossier.Date");
    parameters.setParameter("date2", "Dossier.Date");

    return parameters;
  }

  /**
   * This helper method initializes a PortalSession and profile using a custom EverestStudio repository (created in
   * getPortalEngineDS).
   * 
   * @return
   * @throws AppException
   */
  private PortalSessionTestFacade initializePortalSession(Parameters params, IService initService) throws Exception {
    // initialize testcase:
    return new PortalSessionTestFacade(getApplication(params, initService));
  }

  private ApplicationTemplate getApplication(Parameters params, IService service) throws Exception {
    IService initService = service;
    if (initService == null) {
      initService = initProfileService();
    }

    Parameters loadParams = new Parameters(true);
    loadParams.setParameter("dossierType", "dossierType");
    loadParams.setParameter("action", "load");
    loadParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    Parameters saveParams = new Parameters(true);
    saveParams.setParameter("dossierType", "dossierType");
    saveParams.setParameter("action", "save");
    saveParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    saveParams.setParameter("transient_entities", "Address");
    Parameters initParams = new Parameters(true);
    initParams.setParameter("dossierType", "dossierType");
    initParams.setParameter("action", "init");
    initParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    Parameters updateParams = new Parameters(true);
    updateParams.setParameter("dossierType", "dossierType");
    updateParams.setParameter("action", "update");
    updateParams.setParameter("dossierIdAttribute", "Dossier.Identifier");

    ApplicationTemplate application = new ApplicationTemplate("test");

    EntityTemplate person = application.getMetaModel().addEntity("Person", null, false);
    EntityTemplate address = application.getMetaModel().addEntity("Address", null, false);
    EntityTemplate dossier = application.getMetaModel().addEntity("Dossier", null, true);
    /* EntityTemplate bijlage = */application.getMetaModel().addEntity("Bijlage", null, false);

    application.getMetaModel().addStaticInstance("Bijlage", "rijbewijs");

    person.addAttribute("Name", DataType.STRING, false);
    person.addAttribute("Address", DataType.ENTITY, false).setRelation("Address", null);
    person.addRelation("Bijlage", false, "Bijlage", "Person", false);

    address.addAttribute("Street", DataType.STRING, false);
    dossier.addAttribute("Identifier", DataType.INTEGER, false);
    dossier.addAttribute("Name", DataType.STRING, false);
    dossier.addAttribute("Person", DataType.ENTITY, false).setRelation("Person", null);
    dossier.addAttribute("Date", DataType.DATE, false);

    application.getComposer().addPage("page").addContainer("container").getContainer().addButton("ok");

    application.getFlowEngine().addFlow("start").addServiceCall("init_profile").addPage("page")
        .addServiceCall("test-service", "DossierManager", params).addPage("page");
    application.getFlowEngine().addFlow("save_init_load").addServiceCall("init_profile").addPage("page")
        .addServiceCall("save_dossier", "DossierManager", saveParams).addPage("page")
        .addServiceCall("init_dossier", "DossierManager", initParams).addPage("page")
        .addServiceCall("load_dossier", "DossierManager", loadParams).addPage("page")
        .addServiceCall("update_dossier", "DossierManager", updateParams).addPage("page");

    application.getFactoryManager().getServiceFactory().addService("DossierManager", dossierManager);
    application.getFactoryManager().getServiceFactory().addService("init_profile", initService);

    return application;
  }

  private IService initProfileService() {
    return new IService() {
      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        IEntityInstance dossier = context.getProfile().getSingletonInstance("Dossier", true);
        dossier.setValue("Name", "test");
        dossier.setValue("Date", DateValue.createToday());
        IEntityInstance person = context.getProfile().createInstance("Person");
        person.setValue("Name", "Jon");
        dossier.setValue("Person", person);
        return new ServiceResult();
      }
    };
  }

  private IService initDossierIdService(final int selectedDossierId) {
    return new IService() {

      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        IEntityInstance dossier = context.getProfile().getSingletonInstance("Dossier", true);
        dossier.setValue("Identifier", new IntegerValue(selectedDossierId));
        return new ServiceResult();
      }
    };
  }
}
