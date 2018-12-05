package gov.ca.cwds.cans.security.assessment;

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

  public boolean checkId(String clientId) {
    return super.checkId(clientId);
  }

  public Collection<String> filterIds(Collection<String> ids) {
    return super.filterIds(ids);
  }
}
