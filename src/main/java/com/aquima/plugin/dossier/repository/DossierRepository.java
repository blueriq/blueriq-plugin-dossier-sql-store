package com.aquima.plugin.dossier.repository;

import com.aquima.plugin.dossier.config.DossierConfig;
import com.aquima.plugin.dossier.model.Dossier;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@SuppressWarnings("rawtypes")
public class DossierRepository implements IDossierRepository {

  private final HibernateTemplate tpl;

  @Autowired
  public DossierRepository(@Qualifier(DossierConfig.SESSION_FACTORY_NAME) SessionFactory sessionFactory) {
    tpl = new HibernateTemplate(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Dossier> findByType(String type) {
    DetachedCriteria criteria = DetachedCriteria.forClass(Dossier.class);
    criteria.add(Restrictions.eq("type", type));
    return ((List) tpl.findByCriteria(criteria));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Dossier> find(String dossierType, Map<String, Object> searchValues) {

    Junction junction = Restrictions.conjunction();
    junction.add(Restrictions.eq("type", dossierType));

    if (searchValues.containsKey("feature1")) {
      junction.add(getExpression("feature1", searchValues.get("feature1")));
    }
    if (searchValues.containsKey("feature2")) {
      junction.add(getExpression("feature2", searchValues.get("feature2")));
    }
    if (searchValues.containsKey("feature3")) {
      junction.add(getExpression("feature3", searchValues.get("feature3")));
    }
    if (searchValues.containsKey("feature4")) {
      junction.add(getExpression("feature4", searchValues.get("feature4")));
    }
    if (searchValues.containsKey("date1")) {
      junction.add(Restrictions.eq("date1", searchValues.get("date1")));
    }
    if (searchValues.containsKey("date2")) {
      junction.add(Restrictions.eq("date2", searchValues.get("date2")));
    }

    DetachedCriteria criteria = DetachedCriteria.forClass(Dossier.class);
    criteria.add(junction);

    return ((List) tpl.findByCriteria(criteria));
  }

  @Override
  public Dossier save(Dossier dossier) {
    tpl.saveOrUpdate(dossier);
    return dossier;
  }

  @Override
  public void delete(Dossier dossier) {
    tpl.delete(dossier);
  }

  @Override
  public void delete(Long id) {
    this.delete(findOne(id));
  }

  @Override
  public Dossier findOne(Long id) {
    return tpl.get(Dossier.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Dossier> findAll() {
    return ((List) tpl.findByCriteria(DetachedCriteria.forClass(Dossier.class)));
  }

  // AQR-3446
  private static Criterion getExpression(String feature, Object value) {
    SimpleExpression expression;
    if (value instanceof String) {
      String strValue = (String) value;
      if (strValue.contains("%")) {
        expression = Restrictions.like(feature, strValue);
      } else {
        expression = Restrictions.eq(feature, strValue);
      }
    } else {
      expression = Restrictions.eq(feature, value);
    }

    return expression;
  }
}
