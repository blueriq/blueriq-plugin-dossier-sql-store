package com.aquima.plugin.dossier;

import com.aquima.interactions.foundation.DataType;
import com.aquima.interactions.foundation.Parameters;
import com.aquima.interactions.foundation.types.DateValue;
import com.aquima.interactions.framework.service.ServiceResult;
import com.aquima.interactions.portal.IService;
import com.aquima.interactions.portal.IServiceContext;
import com.aquima.interactions.portal.IServiceResult;
import com.aquima.interactions.portal.ServiceException;
import com.aquima.interactions.profile.IEntityInstance;
import com.aquima.interactions.test.templates.ApplicationTemplate;
import com.aquima.interactions.test.templates.composer.ButtonReference;
import com.aquima.interactions.test.templates.composer.ContainerTemplate;
import com.aquima.interactions.test.templates.composer.PageTemplate;
import com.aquima.interactions.test.templates.flow.PageNodeTemplate;
import com.aquima.interactions.test.templates.model.EntityTemplate;
import com.aquima.plugin.dossier.container.DossierList;
import com.aquima.plugin.dossier.properties.DossierListProperties;
import com.aquima.plugin.dossier.repository.IDossierRepository;
import com.aquima.plugin.dossier.service.DossierManager;

public class DossierTestApp {

  public static ApplicationTemplate getApplication(IDossierRepository repository) throws Exception {
    ApplicationTemplate application = new ApplicationTemplate("test");
    DossierListProperties dossierListProperties = new DossierListProperties();
    createMetaModel(application);

    Parameters loadParams = new Parameters(true);
    loadParams.setParameter("dossierType", "test");
    loadParams.setParameter("action", "load");
    loadParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    Parameters saveParams = new Parameters(true);
    saveParams.setParameter("action", "save");
    saveParams.setParameter("dossierType", "test");
    saveParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    saveParams.setParameter("transient_entities", "Address");
    saveParams.setParameter("feature1", "Person.Name");
    saveParams.setParameter("feature2", "Dossier.Date");
    Parameters initParams = new Parameters(true);
    initParams.setParameter("dossierType", "test");
    initParams.setParameter("action", "init");
    initParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    Parameters initAutosetParams = new Parameters(true);
    initAutosetParams.setParameter("dossierType", "test");
    initAutosetParams.setParameter("action", "init-autoset");
    initAutosetParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    Parameters updateParams = new Parameters(true);
    updateParams.setParameter("dossierType", "test");
    updateParams.setParameter("action", "update");
    updateParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    updateParams.setParameter("feature1", "Person.Name");
    updateParams.setParameter("feature2", "Dossier.Date");
    Parameters deleteParams = new Parameters(true);
    deleteParams.setParameter("action", "delete");
    deleteParams.setParameter("dossierIdAttribute", "Dossier.Identifier");
    deleteParams.setParameter("dossierType", "test");

    application.getFactoryManager().getServiceFactory().addService("DossierManager", new DossierManager(repository));
    application.getFactoryManager().getServiceFactory().addService("init_profile", initProfileService());

    application.getFactoryManager().getContainerFactory().addExpander("DossierListContainer",
        new DossierList(repository, dossierListProperties));

    application.getComposer().addContainer("emptyContainer").addAsset("emptyAsset").getAsset().addText("format",
        "This container is empty");

    PageTemplate page = application.getComposer().addPage("page");
    ContainerTemplate container = page.addContainer("DossierListContainer").getContainer();
    container.setTypeName("DossierListContainer");
    container.addParameter("headers", "feature1=Naam, feature2=Datum");
    container.addParameter("no-result-container", "emptyContainer");
    container.addParameter("dossierIdAttribute", "Dossier.Identifier");
    container.addParameter("dossierType", "test");
    ButtonReference selectDossierButton = container.addButton("SelectDossierButton");
    selectDossierButton.addPresentationStyle("testStyle");
    selectDossierButton.getButton().setDisplayText("Select");
    selectDossierButton.setEvent("Load");
    ButtonReference deleteDossierButton = container.addButton("DeleteDossierButton");
    deleteDossierButton.getButton().setDisplayText("Delete");
    deleteDossierButton.setEvent("Delete");

    // create buttons
    ContainerTemplate buttonContainer = page.addContainer("Actions").getContainer();
    ButtonReference saveButton = buttonContainer.addButton("save_dossier");
    saveButton.getButton().setDisplayText("Save dossier");
    saveButton.addAction("Save");
    saveButton.setEvent("Save");

    ButtonReference initButton = buttonContainer.addButton("init_dossier");
    initButton.getButton().setDisplayText("Init dossier");
    initButton.addAction("Init");
    initButton.setEvent("Init");

    ButtonReference initAutosetButton = buttonContainer.addButton("initAutoset_dossier");
    initAutosetButton.getButton().setDisplayText("Init-autoset dossier");
    initAutosetButton.addAction("Init-autoset");
    initAutosetButton.setEvent("Init-autoset");

    ButtonReference updateButton = buttonContainer.addButton("update_dossier");
    updateButton.getButton().setDisplayText("Update dossier");
    updateButton.addAction("Update");
    updateButton.setEvent("Update");

    // add exit event nodes for the buttons
    page.setExitEvents(new String[] { "Save", "Init", "Init-autoset", "Load", "Delete", "Update" });

    // create initial flow
    application.getFlowEngine().addFlow("start").addServiceCall("init_profile").addFlow("DossierListPageFlow");

    // create flow containing the dossierlist page
    PageNodeTemplate pageNode = application.getFlowEngine().addInternalFlow("DossierListPageFlow").addPage("page");

    // handle events
    pageNode.getEdge("Save").addServiceCall("save_dossier", "DossierManager", saveParams)
        .addFlow("DossierListPageFlow");
    pageNode.getEdge("Init").addServiceCall("init_dossier", "DossierManager", initParams)
        .addFlow("DossierListPageFlow");
    pageNode.getEdge("Init-autoset").addServiceCall("initAutoset_dossier", "DossierManager", initAutosetParams)
        .addFlow("DossierListPageFlow");
    pageNode.getEdge("Load").addServiceCall("load_dossier", "DossierManager", loadParams)
        .addFlow("DossierListPageFlow");
    pageNode.getEdge("Delete").addServiceCall("delete_dossier", "DossierManager", deleteParams)
        .addFlow("DossierListPageFlow");
    pageNode.getEdge("Update").addServiceCall("update_dossier", "DossierManager", updateParams)
        .addFlow("DossierListPageFlow");
    return application;
  }

  private static void createMetaModel(ApplicationTemplate application) {
    EntityTemplate person = application.getMetaModel().addEntity("Person", null, false);
    EntityTemplate address = application.getMetaModel().addEntity("Address", null, false);
    EntityTemplate dossier = application.getMetaModel().addEntity("Dossier", null, true);
    application.getMetaModel().addEntity("Bijlage", null, false);

    application.getMetaModel().addStaticInstance("Bijlage", "rijbewijs");

    person.addAttribute("Name", DataType.STRING, false);
    person.addAttribute("Address", DataType.ENTITY, false).setRelation("Address", null);
    person.addRelation("Bijlage", false, "Bijlage", "Person", false);

    address.addAttribute("Street", DataType.STRING, false);
    dossier.addAttribute("Identifier", DataType.INTEGER, false);
    dossier.addAttribute("Name", DataType.STRING, false);
    dossier.addAttribute("Person", DataType.ENTITY, false).setRelation("Person", null);
    dossier.addAttribute("Date", DataType.DATE, false);
  }

  public static IService initProfileService() {
    return new IService() {

      @Override
      public IServiceResult handle(IServiceContext context) throws ServiceException, Exception {
        IEntityInstance dossier = context.getProfile().getSingletonInstance("Dossier", true);
        dossier.setValue("Name", "test");
        dossier.setValue("Date", DateValue.createToday());
        IEntityInstance person = context.getProfile().createInstance("Person");
        person.setValue("Name", "Jon");
        dossier.setValue("Person", person);
        context.pushActiveInstance(person);
        return new ServiceResult();
      }
    };
  }

  public static Parameters createParams(String action) {
    Parameters parameters = new Parameters(true);
    parameters.setParameter("dossierType", "test");
    parameters.setParameter("dossierIdAttribute", "Dossier.Identifier");
    parameters.setParameter("action", action);
    parameters.setParameter("feature1", "Dossier.Name");
    parameters.setParameter("feature2", "Dossier.Person.Name");
    parameters.setParameter("feature3", "Dossier.Name");
    parameters.setParameter("feature4", "Dossier.Name");
    parameters.setParameter("date1", "Dossier.Date");
    parameters.setParameter("date2", "Dossier.Date");

    return parameters;
  }
}
