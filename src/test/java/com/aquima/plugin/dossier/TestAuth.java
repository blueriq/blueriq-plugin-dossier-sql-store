package com.aquima.plugin.dossier;

import com.aquima.interactions.process.IAuthorizationAlgorithm;
import com.aquima.interactions.process.IAuthorizationAlgorithmEvaluationContext;

/**
 * @author d.roest
 * @since
 * 
 */
public class TestAuth implements IAuthorizationAlgorithm {

  /*
   * (non-Javadoc)
   * 
   * @see com.aquima.interactions.process.IAuthorizationAlgorithm#evaluate(com.aquima.interactions.process.
   * IAuthorizationAlgorithmEvaluationContext)
   */
  @Override
  public String[] evaluate(IAuthorizationAlgorithmEvaluationContext context) {

    return new String[] { "foo" };
  }

}
