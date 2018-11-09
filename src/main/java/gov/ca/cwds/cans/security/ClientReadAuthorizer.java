package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.authorizer.ClientAbstractReadAuthorizer;
import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientAbstractAuthorizationDroolsConfiguration;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

  protected boolean checkInstance(Client client) {
    return this.checkId(client.getIdentifier());
  }

  protected Collection<Client> filterInstances(Collection<Client> instances) {
    throw new UnsupportedOperationException(name() + ".filterInstances");
  }

  protected boolean checkByAssignmentAccessType(AccessType accessType) {
    return !accessType.equals(AccessType.NONE);
  }

  @Override
  protected Collection<String> filterIds(Collection<String> ids) {
    Collection<String> filteredByAssignments =
        clientDao.filterClientIdsByAssignment(ids, staffId());
    if (filteredByAssignments.size() != ids.size()) {
      Set<String> result = new HashSet<>(filteredByAssignments);
      result.addAll(super.filterIds(ids));
      return result;
    } else {
      return ids;
    }
  }

  private boolean checkIdByAssignment(String clientId) {
    AccessType accessType = getAccessType(clientId);
    return checkByAssignmentAccessType(accessType);
  }

  private AccessType getAccessType(String clientId) {
    return clientDao.getAccessTypeByAssignment(clientId, staffId());
  }

  private String staffId() {
    return PrincipalUtils.getStaffPersonId();
  }

  private String name() {
    return getClass().getSimpleName();
  }
}
