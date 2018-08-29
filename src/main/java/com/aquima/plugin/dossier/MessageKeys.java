package com.aquima.plugin.dossier;

/**
 * This simple utility class defines the keys for the messages used by the dossier components like the
 * <strong>DossierList</strong> container and the <strong>DossierManager</strong> service.
 * 
 * @author C. de Meijer
 * @since 5.0
 */
public final class MessageKeys {
  private MessageKeys() {}

  public static final class DossierManager {
    /**
     * Operations LOAD, DELETE and UPDATE require that a dossier has been selected. If this id is not found where
     * expected while one of the aforementioned actions was requested, then this message is used to alert the
     * application designer of this error. The value of this constant is {@value} .
     */
    public static final String NO_DOSSIER_SELECTED = "service.dossiermanager.error.no.dossier.selected";

    /**
     * If for some reason the INIT method fails with an unexpected runtime exception, such as a NullPointer exception,
     * then this message is used to alert the user that the system has encountered an unexpected exception. The detail
     * message (if specified) should point a programmer in the right direction as to what caused the specific exception
     * to occur.
     */
    public static final String INIT_FAILED = "service.dossiermanager.error.init.failed";

    /**
     * If for some reason the UPDATE method fails with an unexpected runtime exception, such as a NullPointer exception,
     * then this message is used to alert the user that the system has encountered an unexpected exception. The detail
     * message (if specified) should point a programmer in the right direction as to what caused the specific exception
     * to occur.
     */
    public static final String UPDATE_FAILED = "service.dossiermanager.error.update.failed";

    /**
     * If for some reason the DELETE method fails with an unexpected runtime exception, such as a NullPointer exception,
     * then this message is used to alert the user that the system has encountered an unexpected exception. The detail
     * message (if specified) should point a programmer in the right direction as to what caused the specific exception
     * to occur.
     */
    public static final String DELETE_FAILED = "service.dossiermanager.error.delete.failed";

    /**
     * If for some reason the SAVE method fails with an unexpected runtime exception, such as a NullPointer exception,
     * then this message is used to alert the user that the system has encountered an unexpected exception. The detail
     * message (if specified) should point a programmer in the right direction as to what caused the specific exception
     * to occur.
     */
    public static final String SAVE_FAILED = "service.dossiermanager.error.save.failed";

    /**
     * If for some reason the LOAD method fails with an unexpected runtime exception, such as a NullPointer exception,
     * then this message is used to alert the user that the system has encountered an unexpected exception. The detail
     * message (if specified) should point a programmer in the right direction as to what caused the specific exception
     * to occur.
     */
    public static final String LOAD_FAILED = "service.dossiermanager.error.load.failed";

    private DossierManager() {}
  }

  public static final class DossierList {
    public static final String EMPTY_MANDATORY_PARAMETER = "container.dossierlist.error.empty.mandatory.parameter";

    public static final String INVALID_HEADER_VALUE = "container.dossierlist.error.invalid.header.value";

    public static final String INVALID_COLUMN_NAME = "container.dossierlist.error.invalid.column.name";

    private DossierList() {}
  }
}
