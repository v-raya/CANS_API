package gov.ca.cwds.cans.service;

import gov.ca.cwds.drools.DroolsConfiguration;
import gov.ca.cwds.drools.DroolsService;
import java.util.Arrays;

public class CansRulesService extends DroolsService {

  private static final String AUTHORIZATION_K_BASE_NAME = "authorization-rules";
  private static final DroolsConfiguration AUTHORIZATION_CONFIG =
      new DroolsConfiguration(
          AUTHORIZATION_K_BASE_NAME, AUTHORIZATION_K_BASE_NAME, AUTHORIZATION_K_BASE_NAME);

  public boolean authorize(Object... facts) {
    return performAuthorizationRules(AUTHORIZATION_CONFIG, Arrays.asList(facts));
  }
}
