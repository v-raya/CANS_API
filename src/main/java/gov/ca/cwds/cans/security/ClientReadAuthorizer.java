package gov.ca.cwds.cans.security;

import static gov.ca.cwds.data.legacy.cms.entity.enums.AccessType.NONE;

import com.google.inject.Inject;
import gov.ca.cwds.authorizer.ClientResultReadAuthorizer;
import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientResultAuthorizationDroolsConfiguration;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientReadAuthorizer extends ClientResultReadAuthorizer {

  private static final Logger LOG = LoggerFactory.getLogger(ClientReadAuthorizer.class);

  @Inject private ClientDao clientDao;

  @Inject
  public ClientReadAuthorizer(
      DroolsAuthorizationService droolsAuthorizationService,
      ClientResultAuthorizationDroolsConfiguration droolsConfiguration) {
    super(droolsAuthorizationService, droolsConfiguration);
  }

  @Override
  protected boolean checkId(String clientId) {
    long startTime = System.currentTimeMillis();
    LOG.info("Authorization: client [{}] started", clientId);
    boolean isAuthorized =
        checkClientResultAccess(clientId)
            || checkIdByAssignment(clientId)
            || checkIdBySupervisorAssignment(clientId);
    LOG.info(
        "Authorization: client [{}] finished with result [{}] in {} ms",
        clientId,
        isAuthorized,
        System.currentTimeMillis() - startTime);
    return isAuthorized;
  }

  protected boolean checkClientResultAccess(String clientId) {
    boolean isClientResultAuthorized = super.checkId(clientId);
    LOG.info(
        "Authorization: client [{}] abstract authorization result [{}]",
        clientId,
        isClientResultAuthorized);
    return isClientResultAuthorized;
  }

  @Override
  protected boolean checkInstance(Client client) {
    return this.checkId(client.getIdentifier());
  }

  @Override
  protected Collection<Client> filterInstances(Collection<Client> instances) {
    throw new UnsupportedOperationException(name() + ".filterInstances");
  }

  private boolean checkByAssignmentAccessType(AccessType accessType) {
    return accessType != NONE;
  }

  @Override
  protected Collection<String> filterIds(Collection<String> ids) {
    Collection<String> filteredByAssignments =
        clientDao.filterClientIdsByAssignment(ids, staffId());
    Collection<String> filteredBySealedSensitive = filterSealedSensitive(ids);
    return mergeClientIds(ids, filteredByAssignments, filteredBySealedSensitive);
  }

  Collection<String> mergeClientIds(
      Collection<String> ids,
      Collection<String> filteredByAssignments,
      Collection<String> filteredBySealedSensitive) {
    if (filteredByAssignments.size() != ids.size()) {
      Set<String> result = new HashSet<>(filteredByAssignments);
      result.addAll(filteredBySealedSensitive);
      return result;
    } else {
      return ids;
    }
  }

  private Collection<String> filterSealedSensitive(Collection<String> ids) {
    return new HashSet<>(super.filterIds(ids));
  }

  private boolean checkIdByAssignment(String clientId) {
    AccessType accessType = getAccessType(clientId);
    boolean isAssignedToClient = checkByAssignmentAccessType(accessType);
    LOG.info(
        "Authorization: client [{}] assignment check result [{}]", clientId, isAssignedToClient);
    return isAssignedToClient;
  }

  private boolean checkIdBySupervisorAssignment(String clientId) {
    AccessType accessType = getAccessTypeBySupervisor(clientId);
    boolean isAssignedToSubordinate = checkByAssignmentAccessType(accessType);
    LOG.info(
        "Authorization: client [{}] subordinates assignment check result [{}]",
        clientId,
        isAssignedToSubordinate);
    return isAssignedToSubordinate;
  }

  AccessType getAccessTypeBySupervisor(String clientId) {
    return clientDao.getAccessTypeBySupervisor(clientId, staffId());
  }

  AccessType getAccessType(String clientId) {
    return clientDao.getAccessTypeByAssignment(clientId, staffId());
  }

  private String staffId() {
    return PrincipalUtils.getStaffPersonId();
  }

  private String name() {
    return getClass().getSimpleName();
  }
}
