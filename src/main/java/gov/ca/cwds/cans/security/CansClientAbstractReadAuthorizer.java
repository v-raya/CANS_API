package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.authorizer.ClientAbstractReadAuthorizer;
import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientAbstractAuthorizationDroolsConfiguration;
import java.util.Collection;

class CansClientAbstractReadAuthorizer extends ClientAbstractReadAuthorizer {
  @Inject
  public CansClientAbstractReadAuthorizer(
      DroolsAuthorizationService droolsAuthorizationService,
      ClientAbstractAuthorizationDroolsConfiguration droolsConfiguration) {
    super(droolsAuthorizationService, droolsConfiguration);
  }

  boolean checkClientId(String clientId) {
    return super.checkId(clientId);
  }

  Collection<String> filterClientIds(Collection<String> ids) {
    return super.filterIds(ids);
  }
}
