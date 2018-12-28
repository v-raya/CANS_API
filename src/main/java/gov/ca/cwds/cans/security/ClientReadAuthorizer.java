package gov.ca.cwds.cans.security;

import static gov.ca.cwds.data.legacy.cms.entity.enums.AccessType.NONE;

import com.google.inject.Inject;
import gov.ca.cwds.authorizer.ClientResultReadAuthorizer;
import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientResultAuthorizationDroolsConfiguration;
import gov.ca.cwds.cans.cache.Cached;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
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

  @Cached
  public AccessType getAccessTypeBySupervisor(String clientId) {
    return clientDao.getAccessTypeBySupervisor(clientId, staffId());
  }

  @Cached
  public AccessType getAccessType(String clientId) {
    return clientDao.getAccessTypeByAssignment(clientId, staffId());
  }

  @Override
  @Cached
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

  private boolean checkClientResultAccess(String clientId) {
    boolean isClientResultAuthorized = super.checkId(clientId);
    LOG.info(
        "Authorization: client [{}] result authorization result [{}]",
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

  @Override
  protected Collection<String> filterIds(Collection<String> ids) {
    throw new UnsupportedOperationException(name() + ".filterIds");
  }

  private boolean checkByAssignmentAccessType(AccessType accessType) {
    return accessType != NONE;
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

  private String staffId() {
    return PrincipalUtils.getStaffPersonId();
  }

  private String name() {
    return getClass().getSimpleName();
  }
}
