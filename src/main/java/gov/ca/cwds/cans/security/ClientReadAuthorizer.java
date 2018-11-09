package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.authorizer.ClientAbstractReadAuthorizer;
import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientAbstractAuthorizationDroolsConfiguration;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.utils.PrincipalUtils;

public class ClientReadAuthorizer extends ClientAbstractReadAuthorizer {

  @Inject private ClientDao clientDao;

  @Inject
  public ClientReadAuthorizer(
      DroolsAuthorizationService droolsAuthorizationService,
      ClientAbstractAuthorizationDroolsConfiguration droolsConfiguration) {
    super(droolsAuthorizationService, droolsConfiguration);
  }

  protected boolean checkId(String clientId) {
    return super.checkId(clientId) || checkIdByAssignment(clientId);
  }

  protected boolean checkByAssignmentAccessType(AccessType accessType) {
    return !accessType.equals(AccessType.NONE);
  }

  private boolean checkIdByAssignment(String clientId) {
    AccessType accessType = getAccessType(clientId);
    return checkByAssignmentAccessType(accessType);
  }

  private AccessType getAccessType(String clientId) {
    String staffId = PrincipalUtils.getStaffPersonId();
    return clientDao.getAccessTypeByAssignment(clientId, staffId);
  }
}
