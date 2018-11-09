package gov.ca.cwds.cans.security;

import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientAbstractAuthorizationDroolsConfiguration;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;

public class ClientWriteAuthorizer extends ClientReadAuthorizer {

  public ClientWriteAuthorizer(
      DroolsAuthorizationService droolsAuthorizationService,
      ClientAbstractAuthorizationDroolsConfiguration droolsConfiguration) {
    super(droolsAuthorizationService, droolsConfiguration);
  }

  protected boolean checkByAssignmentAccessType(AccessType accessType) {
    return accessType.equals(AccessType.RW);
  }
}
