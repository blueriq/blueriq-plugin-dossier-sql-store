package com.aquima.plugin.dossier;

import com.aquima.plugin.dossier.container.DossierContainerTestCase;
import com.aquima.plugin.dossier.service.DossierManagerTestCase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Simple test suite that groups all the individual tests, so they can all be executed at once.
 * 
 * @author O. Kerpershoek
 * @since 5.0
 */
public class DossierTestSuite extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("container-test-suite");

    suite.addTestSuite(DossierContainerTestCase.class);
    suite.addTestSuite(DossierManagerTestCase.class);

    return (suite);
  }
}
