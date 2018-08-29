package com.aquima.plugin.dossier.service;

import com.aquima.interactions.foundation.IPrimitiveValue;
import com.aquima.interactions.foundation.IValue;
import com.aquima.interactions.foundation.exception.AppException;
import com.aquima.interactions.foundation.exception.InvalidStateException;
import com.aquima.interactions.foundation.logging.LogFactory;
import com.aquima.interactions.foundation.logging.Logger;
import com.aquima.interactions.foundation.text.IMultilingualText;
import com.aquima.interactions.foundation.text.MultilingualText;
import com.aquima.interactions.foundation.text.StringUtil;
import com.aquima.interactions.foundation.types.DateValue;
import com.aquima.interactions.foundation.types.IntegerValue;
import com.aquima.interactions.foundation.types.StringValue;
import com.aquima.interactions.framework.service.ServiceResult;
import com.aquima.interactions.metamodel.AttributeReference;
import com.aquima.interactions.metamodel.IEntityDefinition;
import com.aquima.interactions.metamodel.IMetaModel;
import com.aquima.interactions.metamodel.exception.UnknownMessageException;
import com.aquima.interactions.portal.IService;
import com.aquima.interactions.portal.IServiceContext;
import com.aquima.interactions.portal.IServiceResult;
import com.aquima.interactions.portal.ServiceException;
import com.aquima.interactions.portal.util.InstanceLocator;
import com.aquima.interactions.profile.IAttributeValue;
import com.aquima.interactions.profile.IEntityInstance;
import com.aquima.interactions.profile.SourceType;
import com.aquima.interactions.profile.xml.DefaultXmlDelegate;
import com.aquima.interactions.profile.xml.IXmlDelegate;
import com.aquima.interactions.profile.xml.XmlConverter;
import com.aquima.interactions.project.IProject;
import com.aquima.interactions.rule.InferenceContext;
import com.aquima.plugin.dossier.MessageKeys;
import com.aquima.plugin.dossier.ParameterKeys;
import com.aquima.plugin.dossier.RequestKeys;
import com.aquima.plugin.dossier.config.DossierConfig;
import com.aquima.plugin.dossier.model.Dossier;
import com.aquima.plugin.dossier.repository.IDossierRepository;
import com.aquima.plugin.dossier.util.ExceptionHandler;

import com.blueriq.component.api.annotation.AquimaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * The DossierManager service.
 *
 * @author C. de Meijer
 * @since 5.0
 */
@AquimaService("AQ_DossierManager")
@Transactional(transactionManager = DossierConfig.TRANSACTION_MANAGER_NAME)
public class DossierManager implements IService {

  private static final Logger LOG = LogFactory.getLogger(DossierManager.class);

  private final IDossierRepository repository;

  @Autowired
  public DossierManager(IDossierRepository repo) {
    repository = repo;
  }

  @Override
  public IServiceResult handle(final IServiceContext context) throws AppException {
    DossierManagerParameters parameters = new DossierManagerParameters(context.getParameters(), context.getMetaModel());
    DossierAction action = parameters.getAction();

    if (LOG.isDebugEnabled()) {
      LOG.debug("action='" + action + "'");
    }
    if (DossierAction.INIT.equals(action)) {
      try {
        handleInit(context, parameters);
      } catch (final ServiceException e) {
        throw e;
      } catch (final Throwable e) {
        final IProject mm = context.getProject();
        throw new ServiceException(context.getServiceCallName(),
            getMessage(mm, MessageKeys.DossierManager.INIT_FAILED, new String[] { e.getClass().getName() }), e);
      }
    } else if (DossierAction.INIT_AUTOSET.equals(action)) {
      try {
        handleInitAutoDate(context, parameters);
      } catch (final ServiceException e) {
        throw e;
      } catch (final Throwable e) {
        final IProject mm = context.getProject();
        throw new ServiceException(context.getServiceCallName(),
            getMessage(mm, MessageKeys.DossierManager.INIT_FAILED, new String[] { e.getClass().getName() }), e);
      }
    } else if (DossierAction.LOAD.equals(action)) {
      try {
        handleLoad(context, parameters);
      } catch (final ServiceException e) {
        throw e;
      } catch (final Throwable e) {
        final IProject mm = context.getProject();
        throw new ServiceException(context.getServiceCallName(),
            getMessage(mm, MessageKeys.DossierManager.LOAD_FAILED, new String[] { e.getClass().getName() }), e);
      }
    } else if (DossierAction.SAVE.equals(action)) {
      try {
        handleSave(context, parameters);
      } catch (final ServiceException e) {
        throw e;
      } catch (final Throwable e) {
        final IProject mm = context.getProject();
        throw new ServiceException(context.getServiceCallName(),
            getMessage(mm, MessageKeys.DossierManager.SAVE_FAILED, new String[] { e.getClass().getName() }), e);
      }
    } else if (DossierAction.DELETE.equals(action)) {
      try {
        handleDelete(context, parameters);
      } catch (final ServiceException e) {
        throw e;
      } catch (final Throwable e) {
        final IProject mm = context.getProject();
        throw new ServiceException(context.getServiceCallName(),
            getMessage(mm, MessageKeys.DossierManager.DELETE_FAILED, new String[] { e.getClass().getName() }), e);
      }
    } else if (DossierAction.UPDATE.equals(action)) {
      try {
        handleUpdate(context, parameters);
      } catch (final ServiceException e) {
        throw e;
      } catch (final Throwable e) {
        final IProject mm = context.getProject();
        throw new ServiceException(context.getServiceCallName(),
            getMessage(mm, MessageKeys.DossierManager.UPDATE_FAILED, new String[] { e.getClass().getName() }), e);
      }
    } else {
      throw new InvalidStateException("Unsupported action: " + action);
    }

    return new ServiceResult();
  }

  private void handleInit(final IServiceContext context, DossierManagerParameters parameters) throws AppException {
    final IMetaModel mm = context.getMetaModel();
    final InferenceContext profile = context.getProfile();
    final IEntityDefinition[] es = mm.getEntityDefinitions(true);
    for (IEntityDefinition entity : es) {

      if (parameters.shouldDeletableInstances(entity.getName())) {
        continue;
      }

      final IEntityInstance[] instances = profile.getAllInstancesForEntity(entity.getName(), true);
      for (IEntityInstance instance : instances) {
        if (!instance.isStaticInstance()) {
          profile.deleteInstance(instance, true);
        }
      }
    }
  }

  private void handleInitAutoDate(final IServiceContext context, DossierManagerParameters parameters)
      throws AppException {
    handleInit(context, parameters);

    // initialize the date(s)
    final String featureDate1 = context.getParameter(ParameterKeys.DossierManager.DATE_1);

    if (!StringUtil.isEmpty(featureDate1)) {
      InstanceLocator il = new InstanceLocator(context.getProfile());
      IEntityInstance ei = il.getInstanceOf(featureDate1);
      ei.setValue(featureDate1, DateValue.createToday());
    }

    final String featureDate2 = context.getParameter(ParameterKeys.DossierManager.DATE_2);

    if (!StringUtil.isEmpty(featureDate2)) {
      InstanceLocator il = new InstanceLocator(context.getProfile());
      IEntityInstance ei = il.getInstanceOf(featureDate2);
      ei.setValue(featureDate2, DateValue.createToday());
    }
  }

  private void handleLoad(final IServiceContext context, DossierManagerParameters parameters) throws AppException {
    // TODO: Might want to refactor this to use pagescope for security reasons.
    String paramDossierId = (String) context.getRequestScope().getAttribute(RequestKeys.DOSSIER_ID);
    if (StringUtil.isEmpty(paramDossierId)) {
      // if the id was not on the requestscope, get it from the profile
      IntegerValue idFromProfile = getDossierId(context, parameters);
      if (idFromProfile.isUnknown()) {
        context.addErrorMessage(null, MessageKeys.DossierManager.NO_DOSSIER_SELECTED, null);
        return;
      }
      paramDossierId = idFromProfile.stringValue();
    }

    final long id = Long.parseLong(paramDossierId);

    Dossier dossier = repository.findOne(Long.valueOf(id));
    if (!StringUtil.equal(dossier.getType(), parameters.getDossierType())) {
      context.addErrorMessage(null, MessageKeys.DossierManager.NO_DOSSIER_SELECTED, null);
      return;
    }

    XmlConverter converter = new XmlConverter(context.getMetaModel());

    converter.importXml(context.getProfile(), dossier.getProfileXml());

    setDossierId(context, new IntegerValue(dossier.getId()), parameters);
  }

  private void handleSave(final IServiceContext context, DossierManagerParameters parameters) throws AppException {
    Dossier dossier = createDossierModel(context, parameters, IntegerValue.UNKNOWN);

    repository.save(dossier);

    setDossierId(context, new IntegerValue(dossier.getId()), parameters);

    String id = parameters.getIdAttribute().getFullname();
    // AQR-3840: DossierManager plugin: dossierIdAttribute cannot be saved as feature
    if (id.equals(context.getParameter(ParameterKeys.DossierManager.FEATURE_1))
        || id.equals(context.getParameter(ParameterKeys.DossierManager.FEATURE_2))
        || id.equals(context.getParameter(ParameterKeys.DossierManager.FEATURE_3))
        || id.equals(context.getParameter(ParameterKeys.DossierManager.FEATURE_4))) {
      // Only if the id is used as feature, save the update the dossier again
      Dossier updatedDossier = createDossierModel(context, parameters, new IntegerValue(dossier.getId()));
      updatedDossier.setId(dossier.getId());
      repository.save(updatedDossier);
    }
  }

  private void handleUpdate(final IServiceContext context, DossierManagerParameters parameters) throws AppException {
    IntegerValue dossierId = getDossierId(context, parameters);

    if (dossierId.isUnknown()) {
      context.addErrorMessage(null, MessageKeys.DossierManager.NO_DOSSIER_SELECTED, null);
      return;
    }

    Dossier dossier = createDossierModel(context, parameters, dossierId);

    dossier.setId(Long.valueOf(dossierId.longValue()));

    repository.save(dossier);
  }

  private void handleDelete(final IServiceContext context, DossierManagerParameters parameters) throws AppException {
    String paramDossierId = (String) context.getRequestScope().getAttribute(RequestKeys.DOSSIER_ID);
    if (StringUtil.isEmpty(paramDossierId)) {
      // if the id was not on the requestscope, get it from the profile
      IntegerValue idFromProfile = getDossierId(context, parameters);
      if (idFromProfile.isUnknown()) {
        context.addErrorMessage(null, MessageKeys.DossierManager.NO_DOSSIER_SELECTED, null);
        return;
      }
      paramDossierId = idFromProfile.stringValue();
    }
    try {
      long id = Long.parseLong(paramDossierId);
      repository.delete(repository.findOne(id));
    } catch (final NumberFormatException e) {
      // Geen geldig nummer? Dan doen we helemaal niks.
      ExceptionHandler.ignore(e);
    }
  }

  private Dossier createDossierModel(final IServiceContext context, DossierManagerParameters parameters,
      IntegerValue dossierId) throws AppException {
    InstanceLocator locator = new InstanceLocator(context.getProfile());
    final String xml = generateXml(context, parameters);

    final Dossier dossier;

    if (dossierId.isUnknown()) {
      dossier = new Dossier();
    } else {
      dossier = repository.findOne(Long.valueOf(dossierId.longValue()));
    }

    dossier.setType(parameters.getDossierType());

    dossier.setProfileXml(xml);
    final String f1 = context.getParameter(ParameterKeys.DossierManager.FEATURE_1);
    if (f1 != null && f1.length() != 0) {
      IValue refVal = locator.getValueOf(f1).getValue();
      if (!refVal.isUnknown()) {
        dossier.setFeature1(StringValue.valueOf(refVal).stringValue());
      }
    }
    final String f2 = context.getParameter(ParameterKeys.DossierManager.FEATURE_2);
    if (f2 != null && f2.length() != 0) {
      IValue refVal = locator.getValueOf(f2).getValue();

      if (!refVal.isUnknown()) {
        dossier.setFeature2(StringValue.valueOf(refVal).stringValue());
      }
    }
    final String f3 = context.getParameter(ParameterKeys.DossierManager.FEATURE_3);
    if (f3 != null && f3.length() != 0) {
      IValue refVal = locator.getValueOf(f3).getValue();

      if (!refVal.isUnknown()) {
        dossier.setFeature3(StringValue.valueOf(refVal).stringValue());
      }
    }
    final String f4 = context.getParameter(ParameterKeys.DossierManager.FEATURE_4);
    if (f4 != null && f4.length() != 0) {
      IValue refVal = locator.getValueOf(f4).getValue();

      if (!refVal.isUnknown()) {
        dossier.setFeature4(StringValue.valueOf(refVal).stringValue());
      }
    }
    final String pd1 = context.getParameter(ParameterKeys.DossierManager.DATE_1);
    if (pd1 != null && pd1.length() != 0) {
      IValue refVal = locator.getValueOf(pd1).getValue();

      if (!refVal.isUnknown()) {
        dossier.setDate1(DateValue.valueOf(refVal).dateValue());
      }
    }
    final String pd2 = context.getParameter(ParameterKeys.DossierManager.DATE_2);
    if (pd2 != null && pd2.length() != 0) {
      IValue refVal = locator.getValueOf(pd2).getValue();

      if (!refVal.isUnknown()) {
        dossier.setDate2(DateValue.valueOf(refVal).dateValue());
      }
    }

    return dossier;
  }

  private String generateXml(IServiceContext context, final DossierManagerParameters parameters) throws AppException {
    IXmlDelegate visitor = new DefaultXmlDelegate() {

      @Override
      public boolean includeEntity(final IEntityDefinition ed) {
        return !parameters.isTransientEntity(ed.getName());
      }

      @Override
      public boolean includeAttribute(IAttributeValue attributeValue) {
        if (attributeValue.isUnknown()) {
          return false;
        }
        return (SourceType.USER.equals(attributeValue.getSourceType()));
      }
    };

    XmlConverter converter = new XmlConverter(context.getMetaModel(), visitor);
    InferenceContext profile = context.getProfile();

    return converter.exportXml(profile, "dossier");
  }

  private IntegerValue getDossierId(IServiceContext context, DossierManagerParameters parameters) throws AppException {
    AttributeReference refeference = parameters.getIdAttribute();
    InferenceContext profile = context.getProfile();
    IEntityInstance instance = profile.getActiveInstance(refeference.getEntityName());

    return IntegerValue.valueOf(instance.getValue(refeference.getAttributeName()));
  }

  private void setDossierId(IServiceContext context, IPrimitiveValue dossierId, DossierManagerParameters parameters)
      throws AppException {
    AttributeReference reference = parameters.getIdAttribute();
    InferenceContext profile = context.getProfile();
    IEntityInstance instance = profile.getActiveInstance(reference.getEntityName());

    if (dossierId == null || dossierId.isUnknown()) {
      instance.clearValue(reference.getAttributeName());
    } else {
      instance.setValue(reference.getAttributeName(), dossierId);
    }
  }

  private IMultilingualText getMessage(IProject mm, String key, String[] params) {
    try {
      return mm.getMessage(key, params);
    } catch (UnknownMessageException e) {
      return new MultilingualText(key);
    }
  }
}
