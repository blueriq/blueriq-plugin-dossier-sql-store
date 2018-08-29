package com.aquima.plugin.dossier.container;

import com.aquima.interactions.composer.ElementType;
import com.aquima.interactions.composer.model.Button;
import com.aquima.interactions.composer.model.Container;
import com.aquima.interactions.composer.model.ContentStyle;
import com.aquima.interactions.composer.model.Element;
import com.aquima.interactions.composer.model.RendererProperty;
import com.aquima.interactions.composer.model.definition.AbstractReference;
import com.aquima.interactions.composer.model.definition.ContainerDefinition;
import com.aquima.interactions.foundation.IPrimitiveValue;
import com.aquima.interactions.foundation.IValue;
import com.aquima.interactions.foundation.exception.AppException;
import com.aquima.interactions.foundation.logging.LogFactory;
import com.aquima.interactions.foundation.logging.Logger;
import com.aquima.interactions.foundation.text.StringUtil;
import com.aquima.interactions.foundation.types.DateValue;
import com.aquima.interactions.framework.container.model.table.Table;
import com.aquima.interactions.framework.container.model.table.TableCell;
import com.aquima.interactions.framework.container.model.table.TableHeader;
import com.aquima.interactions.framework.container.model.table.TableRow;
import com.aquima.interactions.metamodel.AttributeReference;
import com.aquima.interactions.metamodel.IDomainDefinition;
import com.aquima.interactions.portal.IContainerContext;
import com.aquima.interactions.portal.IContainerExpander;
import com.aquima.interactions.portal.IPortalContext;
import com.aquima.interactions.portal.InvocationType;
import com.aquima.interactions.profile.IEntityInstance;
import com.aquima.interactions.profile.exception.UnknownInstanceException;
import com.aquima.plugin.dossier.ParameterKeys;
import com.aquima.plugin.dossier.config.DossierConfig;
import com.aquima.plugin.dossier.container.callback.DossierButtonCallback;
import com.aquima.plugin.dossier.model.Dossier;
import com.aquima.plugin.dossier.properties.DossierListProperties;
import com.aquima.plugin.dossier.repository.IDossierRepository;

import com.blueriq.component.api.annotation.AquimaExpander;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DossierList dynamic container.
 *
 * @author C. de Meijer
 * @since 5.0
 */
@AquimaExpander("AQ_DossierList")
@Transactional(transactionManager = DossierConfig.TRANSACTION_MANAGER_NAME)
public class DossierList implements IContainerExpander {

  private static final Logger LOG = LogFactory.getLogger(DossierList.class);

  private final IDossierRepository repository;
  private final DossierListProperties properties;

  @Autowired
  public DossierList(IDossierRepository repository, DossierListProperties properties) {
    this.repository = repository;
    this.properties = properties;
  }

  @Override
  public Container expand(Container container, ContainerDefinition definition, IContainerContext context)
      throws Exception {
    DossierListParameters parameters = new DossierListParameters(context);
    DossierButtons buttons = findButtons(definition, context);

    Map<String, Object> searchValues = obtainSearchValues(context, parameters);
    List<Dossier> dossiers = queryForDossiers(parameters.getDossierType(), searchValues);
    dossiers = this.fixDomainValues(dossiers, parameters);

    if (dossiers.isEmpty() && parameters.getEmptyContainer() != null) {
      return context.getElementComposer().expandContainer(parameters.getEmptyContainer());
    }

    TableHeader header = new TableHeader(obtainTableHeaders(parameters.getHeaderColumns(), buttons));
    Table table = new Table(header);
    table.setContentStyle(ContentStyle.valueOf("dossierlist"));
    table.setProperty("pagingsize", new RendererProperty(properties.getPagingsize()));
    for (Dossier dossier : dossiers) {
      table.addRow(obtainDossierRow(context, buttons, dossier, parameters));
    }
    container.addElement(table);
    return container;
  }

  private List<Dossier> queryForDossiers(String dossierType, Map<String, Object> searchValues) {
    List<Dossier> dossiers;
    if (searchValues.size() == 0) {
      dossiers = repository.findByType(dossierType);
    } else {
      dossiers = repository.find(dossierType, searchValues);
    }
    return dossiers;
  }

  private List<Dossier> fixDomainValues(List<Dossier> dossiers, DossierListParameters parameters) {
    HeaderColumnDefinition[] headers = parameters.getHeaderColumns();
    List<Dossier> result = new ArrayList<Dossier>(dossiers.size());

    for (Dossier model : dossiers) {
      result.add(this.fixDomainValues(model, headers));
    }

    return result;
  }

  private Dossier fixDomainValues(Dossier model, HeaderColumnDefinition... headers) {
    for (HeaderColumnDefinition header : headers) {
      IDomainDefinition domain = header.getDomain();

      if (domain == null) {
        // value does not need to be updated for this domain
        continue;
      }

      IPrimitiveValue dossierValue = model.getValue(header.getFeature());

      if (dossierValue == null || dossierValue.isUnknown()) {
        continue;
      }

      dossierValue = getDomainValue(domain, dossierValue);

      model.setValue(header.getFeature(), dossierValue);
    }

    return model;
  }

  private IPrimitiveValue getDomainValue(IDomainDefinition domain, IPrimitiveValue dossierValue) {
    String strDossierValue = dossierValue.stringValue();

    for (IPrimitiveValue domainValue : domain.getValues()) {
      String strDomainValue = domainValue.stringValue();

      if (strDomainValue.equalsIgnoreCase(strDossierValue)) {
        return domainValue;
      }
    }

    return dossierValue;
  }

  private Map<String, Object> obtainSearchValues(final IContainerContext containerContext,
      DossierListParameters parameters) throws AppException {
    HashMap<String, Object> searchValues = new HashMap<String, Object>(16);
    SearchColumnDefinition[] searchColumns = parameters.getSearchColumns();

    for (SearchColumnDefinition searchColumn : searchColumns) {
      AttributeReference reference = searchColumn.getAttribute();

      // TODO We could use an instance locator here to add more flexibility for the search values
      try {
        IEntityInstance instance = containerContext.getActiveInstance(reference.getEntityName());
        IValue attrValue = instance.getValue(reference.getAttributeName());

        if (attrValue.isUnknown()) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("[obtainSearchValues] No value available for search column: " + searchColumn
                + " (The collumn will not be used to narrow the results)");
          }
          continue;
        }

        if (searchColumn.isDateColumn()) {
          Date dateValue = attrValue.dateValue();

          searchValues.put(searchColumn.getFeature(), dateValue);
        } else {
          String strValue = attrValue.stringValue();

          // User input '%' are removed. User input '*' is replaced by '%'.
          strValue = StringUtil.replaceInString(strValue, "%", "");
          strValue = StringUtil.replaceInString(strValue, "*", "%");

          searchValues.put(searchColumn.getFeature(), strValue);
        }
      } catch (UnknownInstanceException error) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("[obtainSearchValues] No active instance found for search column: " + searchColumn
              + " (The column will not be used to narrow the results)");
        }
      }
    }

    return searchValues;
  }

  private Serializable[] obtainTableHeaders(HeaderColumnDefinition[] headers, DossierButtons buttons)
      throws AppException {
    ArrayList<TableCell> result = new ArrayList<TableCell>();

    // add extra header cells for the buttons
    result.add(new TableCell(null));

    if (buttons.hasDeleteButton()) {
      result.add(new TableCell(null));
    }

    for (HeaderColumnDefinition headerColumn : headers) {
      TableCell cell = new TableCell(headerColumn.getMessage());
      result.add(cell);
    }

    return result.toArray(new Serializable[result.size()]);
  }

  /**
   * Creates a row for the dossier with select and (optionally) a delete button. Columns in the row come from the
   * header.
   *
   * @param context The container context, for registering the button callbacks.
   * @param buttons Select and delete buttons. These buttons are duplicated, and the duplicates are put on the row.
   * @param dossier Dossier for this row.
   * @param parameters Parameters passed to this container. These are passed to the button callbacks to call the
   *        DossierService.
   * @return A complete table row for the dossier.
   */
  private TableRow obtainDossierRow(IContainerContext context, DossierButtons buttons, Dossier dossier,
      DossierListParameters parameters) {
    HeaderColumnDefinition[] headers = parameters.getHeaderColumns();
    List<TableCell> cells = new ArrayList<TableCell>();

    cells.add(new TableCell(createButton(context, buttons.getSelectButton(), dossier)));

    if (buttons.hasDeleteButton()) {
      cells.add(new TableCell(createButton(context, buttons.getDeleteButton(), dossier)));
    }

    for (HeaderColumnDefinition header : headers) {
      String feature = header.getFeature();

      TableCell featureCell;
      if (ParameterKeys.DossierManager.FEATURE_1.equalsIgnoreCase(feature)) {
        featureCell = new TableCell(dossier.getFeature1());
      } else if (ParameterKeys.DossierManager.FEATURE_2.equalsIgnoreCase(feature)) {
        featureCell = new TableCell(dossier.getFeature2());
      } else if (ParameterKeys.DossierManager.FEATURE_3.equalsIgnoreCase(feature)) {
        featureCell = new TableCell(dossier.getFeature3());
      } else if (ParameterKeys.DossierManager.FEATURE_4.equalsIgnoreCase(feature)) {
        featureCell = new TableCell(dossier.getFeature4());
      } else if (ParameterKeys.DossierManager.DATE_1.equalsIgnoreCase(feature)) {
        featureCell = new TableCell(new DateValue(dossier.getDate1()));
      } else if (ParameterKeys.DossierManager.DATE_2.equalsIgnoreCase(feature)) {
        featureCell = new TableCell(new DateValue(dossier.getDate1()));
      } else {
        throw new AppException("Invalid feature '" + feature + "', the header definition for parameter '"
            + ParameterKeys.DossierList.HEADERS + "' should be in the format '<feature>=<domain@message>'.");
      }
      cells.add(featureCell);
    }

    return new TableRow(dossier.getId(), cells.toArray(new Serializable[cells.size()]));
  }

  /**
   * Creates a button and registers a callback
   */
  private Button createButton(IContainerContext context, Button button, Dossier dossier) {
    Button duplicatedButton = (Button) button.duplicate();
    context.registerCallback(duplicatedButton, new DossierButtonCallback(dossier.getId()), InvocationType.ON_EVENT);
    return duplicatedButton;
  }

  /**
   * Creates buttons from the container definition. These buttons can be duplicated for every row in the container.
   */
  private DossierButtons findButtons(ContainerDefinition definition, IPortalContext context) {
    if (definition.getContainmentCount() < 1 || definition.getContainmentCount() > 2) {
      throw new AppException("The DossierList container '" + definition.getName()
          + "' should have one or two buttons in its containment, a 'Select Dossier' button and optionally 'Delete Dossier' button.");
    }

    Button selectButton = createButtonFromReference(context, definition.getContainmentAt(0));

    Button deleteButton = null;
    if (definition.getContainmentCount() == 2) {
      deleteButton = createButtonFromReference(context, definition.getContainmentAt(1));
    }

    return new DossierButtons(selectButton, deleteButton);
  }

  private Button createButtonFromReference(IPortalContext context, AbstractReference elementReference) {
    if (elementReference.getTargetType() != ElementType.BUTTON) {
      throw new AppException("DossierList container can only have buttons in its containment, '"
          + elementReference.getTargetName() + "' is not a button.");
    }

    Element[] elements = context.getElementComposer().expandReference(elementReference);
    // AQR-2347: Een format op een knop binnen de AQ_DossierList wordt niet gerenderd in de runtime
    if (elements.length != 1) {
      throw new AppException(
          "The reference of a button in the DossierList container may only result in one button except: "
              + elements.length);
    }
    return (Button) elements[0];
  }
}
