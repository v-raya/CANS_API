package gov.ca.cwds.cans.security;

import static gov.ca.cwds.data.legacy.cms.entity.enums.AccessType.NONE;

import com.google.inject.Inject;
import gov.ca.cwds.authorizer.ClientAbstractReadAuthorizer;
import gov.ca.cwds.authorizer.drools.DroolsAuthorizationService;
import gov.ca.cwds.authorizer.drools.configuration.ClientAbstractAuthorizationDroolsConfiguration;
import gov.ca.cwds.data.dao.cms.CountyDeterminationDao;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientReadAuthorizer extends ClientAbstractReadAuthorizer {

  private static final Logger LOG = LoggerFactory.getLogger(ClientReadAuthorizer.class);

  @Inject private ClientDao clientDao;
  @Inject private CountyDeterminationDao countyDeterminationDao;

  @Inject
  public ClientReadAuthorizer(
      DroolsAuthorizationService droolsAuthorizationService,
      ClientAbstractAuthorizationDroolsConfiguration droolsConfiguration) {
    super(droolsAuthorizationService, droolsConfiguration);
  }

  @Override
  protected boolean checkId(String clientId) {
    long startTime = System.currentTimeMillis();
    LOG.info("Authorization: client [{}] started", clientId);
    boolean isAuthorized =
        checkSealedSensitive(clientId)
            || checkIdByAssignment(clientId)
            || checkIdBySupervisorAssignment(clientId);
    LOG.info(
        "Authorization: client [{}] finished with result [{}] in {} ms",
        clientId,
        isAuthorized,
        System.currentTimeMillis() - startTime);
    return isAuthorized;
  }

  protected boolean checkSealedSensitive(String clientId) {
    boolean isSealedSensitive = checkByCounty(clientId) && checkClientAbstractAccess(clientId);
    LOG.info(
        "Authorization: client [{}] county and abstract result [{}]", clientId, isSealedSensitive);
    return isSealedSensitive;
  }

  private boolean checkClientAbstractAccess(String clientId) {
    boolean isClientAbstractAuthorized = super.checkId(clientId);
    LOG.info(
        "Authorization: client [{}] abstract authorization result [{}]",
        clientId,
        isClientAbstractAuthorized);
    return isClientAbstractAuthorized;
  }

  @Override
  protected boolean checkInstance(Client client) {
    return this.checkId(client.getIdentifier());
  }

  @Override
  protected Collection<Client> filterInstances(Collection<Client> instances) {
    throw new UnsupportedOperationException(name() + ".filterInstances");
  }

  private boolean checkByCounty(String clientId) {
    Collection<Short> counties = countyDeterminationDao.getClientCounties(clientId);
    boolean hasEmptyOrSameCounty = counties.isEmpty() || counties.contains(staffCounty());
    LOG.info(
        "Authorization: client [{}] has no or the same county [{}] result [{}]",
        clientId,
        counties,
        hasEmptyOrSameCounty);
    return hasEmptyOrSameCounty;
  }

  private boolean checkByAssignmentAccessType(AccessType accessType) {
    return accessType != NONE;
  }

  @Override
  protected Collection<String> filterIds(Collection<String> ids) {
    Collection<String> filteredByAssignments =
        clientDao.filterClientIdsByAssignment(ids, staffId());
    if (filteredByAssignments.size() != ids.size()) {
      Set<String> result = new HashSet<>(filteredByAssignments);
      result.addAll(filterSealedSensitive(ids));
      return result;
    } else {
      return ids;
    }
  }

  private Collection<String> filterSealedSensitive(Collection<String> ids) {
    Collection<String> filteredByAbstractRules = new HashSet<>(super.filterIds(ids));
    Collection<String> filteredByCounties = filterByCounties(ids);
    filteredByAbstractRules.retainAll(filteredByCounties);
    return filteredByAbstractRules;
  }

  private Collection<String> filterByCounties(Collection<String> ids) {
    Map<String, List<Short>> countiesMap = countyDeterminationDao.getClientCountiesMap(ids);
    Short staffCounty = staffCounty();
    Collection<String> result = new HashSet<>();
    countiesMap.forEach(
        (id, counties) -> {
          if (counties.isEmpty() || counties.contains(staffCounty)) {
            result.add(id);
          }
        });
    return result;
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

  private Short staffCounty() {
    return Short.valueOf(PrincipalUtils.getPrincipal().getCountyCwsCode());
  }

  private String name() {
    return getClass().getSimpleName();
  }
}
