package com.aquima.plugin.dossier.container;

import com.aquima.interactions.composer.IElement;
import com.aquima.interactions.composer.IPage;
import com.aquima.interactions.composer.model.Button;
import com.aquima.interactions.composer.model.Container;
import com.aquima.interactions.composer.model.FailedElement;
import com.aquima.interactions.foundation.Parameters;
import com.aquima.interactions.foundation.exception.AppException;
import com.aquima.interactions.foundation.text.IMultilingualText;
import com.aquima.interactions.framework.container.model.table.Table;
import com.aquima.interactions.framework.container.model.table.TableHeader;
import com.aquima.interactions.framework.container.model.table.TableRow;
import com.aquima.interactions.test.templates.ApplicationTemplate;
import com.aquima.interactions.test.templates.composer.ContainerTemplate;
import com.aquima.interactions.test.templates.session.PortalSessionTestFacade;
import com.aquima.plugin.dossier.ParameterKeys;
import com.aquima.plugin.dossier.properties.DossierListProperties;
import com.aquima.plugin.dossier.repository.TestDossierRepository;

import junit.framework.TestCase;

/**
 * Test case for the dossier container.
 *
 * @author j.van.leuven
 * @since 5.0
 */
public class DossierContainerTestCase extends TestCase {

  private PortalSessionTestFacade mSession;
  private final TestDossierRepository testRepository = new TestDossierRepository();

  private DossierList dossierList;

  @Override
  public void setUp() throws Exception {
    DossierListProperties properties = new DossierListProperties();
    dossierList = new DossierList(testRepository, properties);
  }
  //
  // @Override
  // public void tearDown()
  // {
  // // testRepository = null;
  // if (this.mSession != null) {
  // this.mSession.close();
  // }
  // this.mSession = null;
  // }

  public void testDossierContent() throws Exception {
    Parameters parameters = new Parameters(true);
    parameters.setParameter(ParameterKeys.DossierList.HEADERS, "feature1=header1, feature2=header2, date1=App date");
    parameters.setParameter(ParameterKeys.DossierList.DOSSIERTYPE, "dossiertype");
    this.mSession = this.initializePortalSession(parameters);

    // start flow:
    this.mSession.startFlow("start");
    this.testRepository.assertOnlyEvent(TestDossierRepository.EVENT_FINDALL);
    IPage resultPage = this.mSession.getCurrentPage();

    assertNotNull(resultPage);
    assertEquals("page must contain 1 element", 1, resultPage.getElementCount());
    assertEquals("page must contain a container", Container.class, resultPage.getElement(0).getClass());
    assertEquals("container must contain a table", Table.class,
        ((Container) resultPage.getElement(0)).getElement(0).getClass());
    Table dossiertable = (Table) ((Container) resultPage.getElement(0)).getElement(0);
    assertEquals("DossierContainer must contain four headers", 4, dossiertable.getHeader().getCellCount());
    assertEquals("DossierContainer must contain two dossiers", 2, dossiertable.getRowCount());

    TableHeader headers = dossiertable.getHeader();
    IMultilingualText header0value = ((IMultilingualText) headers.getCellAt(0).getValue());
    IMultilingualText header1value = ((IMultilingualText) headers.getCellAt(1).getValue());

    assertNull("first header must be empty", header0value);
    assertEquals("second header must be first header parameter", "header1", header1value.getValue(""));

    TableRow dossierRow = dossiertable.getRowAt(0);
    assertEquals("row should have a button", Button.class, dossierRow.getCellAt(0).getValue().getClass());
    assertEquals("second column must be filled", "feature1", dossierRow.getCellAt(1).getValue());
    assertEquals("third column must be filled", "feature2", dossierRow.getCellAt(2).getValue());

    TableRow dossierRow2 = dossiertable.getRowAt(1);
    assertEquals("row should have a button", Button.class, dossierRow2.getCellAt(0).getValue().getClass());
    assertEquals("second column must be filled", "balat", dossierRow2.getCellAt(1).getValue());
    assertEquals("third column must be filled", "gdfhjgfjdkg", dossierRow2.getCellAt(2).getValue());
  }

  public void testInvalidParameters1() throws Exception {
    Parameters parameters = new Parameters(true);
    this.mSession = this.initializePortalSession(parameters);

    // start flow:
    this.mSession.startFlow("start");
    this.testRepository.assertNrOfEvents(0);

    IPage page = this.mSession.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("page must contain 1 element", 1, page.getElementCount());
    IElement e1 = page.getElement(0);
    assertEquals("first element must be a failed element container", FailedElement.class, e1.getClass());
  }

  public void testInvalidParameters2() throws Exception {
    Parameters parameters = new Parameters(true);
    parameters.setParameter(ParameterKeys.DossierList.HEADERS, "bla, bla");
    this.mSession = this.initializePortalSession(parameters);

    // start flow:
    this.mSession.startFlow("start");
    this.testRepository.assertNrOfEvents(0);

    IPage page = this.mSession.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("page must contain 1 element", 1, page.getElementCount());
    IElement e1 = page.getElement(0);
    assertEquals("first element must be a failed element container", FailedElement.class, e1.getClass());
  }

  public void testInvalidParameters3() throws Exception {
    Parameters parameters = new Parameters(true);
    parameters.setParameter(ParameterKeys.DossierList.HEADERS, "bla=blaa, bla=blaat");
    parameters.setParameter("dossierType", "dossierType");
    this.mSession = this.initializePortalSession(parameters);

    // start flow:
    this.mSession.startFlow("start");
    this.testRepository.assertOnlyEvent(TestDossierRepository.EVENT_FINDALL);

    IPage page = this.mSession.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("page must contain 1 element", 1, page.getElementCount());
    IElement e1 = page.getElement(0);
    assertEquals("first element must be a failed element container", FailedElement.class, e1.getClass());
  }

  public void testMissingDossierType() throws Exception {
    Parameters parameters = new Parameters(true);
    parameters.setParameter(ParameterKeys.DossierList.HEADERS, "bla=blaa, bla=blaat");
    this.mSession = this.initializePortalSession(parameters);

    // start flow:
    this.mSession.startFlow("start");
    this.testRepository.assertNrOfEvents(0);

    IPage page = this.mSession.getCurrentPage(true);
    assertNotNull(page);
    assertEquals("page must contain 1 element", 1, page.getElementCount());
    IElement e1 = page.getElement(0);
    assertEquals("first element must be a failed element container", FailedElement.class, e1.getClass());
  }

  /**
   * This helper method initializes a PortalSession and profile using a custom EverestStudio repository (created in
   * getPortalEngineDS).
   *
   * @return
   * @throws AppException
   */
  private PortalSessionTestFacade initializePortalSession(Parameters params) throws Exception {
    // initialize testcase:
    ApplicationTemplate application = this.getApplication(params);
    return new PortalSessionTestFacade(application);
  }

  private ApplicationTemplate getApplication(Parameters params) throws Exception {
    ApplicationTemplate application = new ApplicationTemplate("test");

    application.getFlowEngine().addFlow("start").addPage("page").addFlow("start");

    ContainerTemplate container = application.getComposer().addPage("page").addContainer("container").getContainer();
    container.addButton("SelectDossier");

    container.setTypeName("DossierList");

    String[] keys = params.getParameterKeys();

    for (String key : keys) {
      String strValue = params.getParameter(key);

      container.addParameter(key, strValue);
    }

    application.getFactoryManager().getContainerFactory().addExpander("DossierList", this.dossierList);

    return application;
  }
}
