package com.aquima.plugin.dossier.util;

import com.aquima.interactions.foundation.logging.LogFactory;
import com.aquima.interactions.foundation.logging.Logger;

/**
 * Deze class biedt een aantal methoden aan die binnen de applicatie gebruikt kunnen worden om Throwables en Exceptions
 * expliciet te negeren. Door deze class te gebruiken, kan op een later tijdstip extra functionaliteit ingebouwd worden
 * om inzicht te verkrijgen in het 'onderwater' gedrag van de applicatie. Een reden om dit te willen is bijvoorbeeld
 * performanceverbetering. Het throwen van excepties kost namelijk relatief veel tijd. Als er in een gedeelte van de
 * code excessief gebruik gemaakt wordt van het exceptiemechanisme, dan zal dit slechter performen dan wanneer er voor
 * gezorgd wordt dat deze excepties niet optreden.
 * 
 * @author <a href="mailto:c.de.meijer@everest.nl">C. de Meijer</a>
 */
public final class ExceptionHandler {
  private static final Logger LOG = LogFactory.getLogger(ExceptionHandler.class);

  public static final boolean LOGGING_ENABLED = false;

  private ExceptionHandler() {}

  /**
   * Negeer de gespecificeerde Exception. Indien de LOGGING_ENABLED setting ge-activeerd is, dan zal een melding van het
   * feit dat deze Exception genegeerd is, gemeld worden in de log van de applicatie.
   * 
   * @param e Een instantie van een Exception die genegeerd moet worden.
   */
  public static void ignore(final Exception e) {
    if (!LOGGING_ENABLED) {
      return;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("An exception has been ignored by the application.", e);
    }
  }

  /**
   * Negeer de gespecificeerde Exception. Indien de LOGGING_ENABLED setting ge-activeerd is, dan zal een melding van het
   * feit dat deze Exception genegeerd is, gemeld worden in de log van de applicatie.
   * 
   * @param e Een instantie van een Exception die genegeerd moet worden.
   * @param warn Boolean om aan te geven dat dit serieus is en er hoe dan ook een warning gelogd moet worden.
   *        Bijvoorbeeld als er in commentaar staat dat 'dit nooit voor kan komen'.
   */
  public static void ignore(final Exception e, final boolean warn) {
    if (!warn && !LOGGING_ENABLED) {
      return;
    }
    if (warn) {
      LOG.warning("An exception has been ignored by the application.", e);
    } else {
      LOG.debug("An exception has been ignored by the application.", e);
    }
  }
}
