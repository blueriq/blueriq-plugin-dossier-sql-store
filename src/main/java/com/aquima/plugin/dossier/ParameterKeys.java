package com.aquima.plugin.dossier;

/**
 * This simple utility class houses all the known parameters used by the dossier components (like DossierList and
 * DossierManager).
 * 
 * @author C. de Meijer
 * @since 5.0
 */
public final class ParameterKeys {
  private ParameterKeys() {}

  /**
   * This private class contains the parameters common to all the dossier components.
   * 
   * @author C. de Meijer
   * @since 5.0
   */
  private abstract static class DossierComponent {
    public static final String DOSSIERTYPE = "dossierType";
    public static final String DOSSIER_ID_ATTRIBUTE = "dossierIdAttribute";
    public static final String FEATURE_1 = "feature1";
    public static final String FEATURE_2 = "feature2";
    public static final String FEATURE_3 = "feature3";
    public static final String FEATURE_4 = "feature4";
    public static final String DATE_1 = "date1";
    public static final String DATE_2 = "date2";

    public static final String[] ALL_FEATURES = { FEATURE_1, FEATURE_2, FEATURE_3, FEATURE_4 };
    public static final String[] ALL_DATES = { DATE_1, DATE_2 };

    private DossierComponent() {}
  }

  /**
   * This class defines the actions for the dossier manager.
   * 
   * @author C. de Meijer
   * @since 5.0
   */
  public static final class DossierManager extends DossierComponent {
    public static final String ACTION = "action";
    public static final String UNDELETABLE_INSTANCES = "undeletable_instances";
    public static final String TRANSIENT_ENTITIES = "transient_entities";

    private DossierManager() {}
  }

  /**
   * This class defines the actions of the dossier list.
   * 
   * @author C. de Meijer
   * @since 5.0
   */
  public static final class DossierList extends DossierComponent {
    public static final String HEADERS = "headers";
    public static final String NO_RESULT_CONTAINER = "no-result-container";
    public static final String[] ALL_COLUMNS = { FEATURE_1, FEATURE_2, FEATURE_3, FEATURE_4, DATE_1, DATE_2 };

    private DossierList() {}
  }

}
