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

public class ClientReadAuthorizer extends ClientAbstractReadAuthorizer {

  @Inject private ClientDao clientDao;
  @Inject private CountyDeterminationDao countyDeterminationDao;

  @Inject
  public ClientReadAuthorizer(
      DroolsAuthorizationService droolsAuthorizationService,
      ClientAbstractAuthorizationDroolsConfiguration droolsConfiguration) {
    super(droolsAuthorizationService, droolsConfiguration);
  }

  protected boolean checkId(String clientId) {
    return checkSealedSensitive(clientId) || checkIdByAssignment(clientId);
  }

  protected boolean checkSealedSensitive(String clientId) {
    return checkByCounty(clientId) && checkClientAbstractAccess(clientId);
  }

  private boolean checkClientAbstractAccess(String clientId) {
    return super.checkId(clientId);
  }

  protected boolean checkInstance(Client client) {
    return this.checkId(client.getIdentifier());
  }

  protected Collection<Client> filterInstances(Collection<Client> instances) {
    throw new UnsupportedOperationException(name() + ".filterInstances");
  }

  private boolean checkByCounty(String clientId) {
    Collection<Short> counties = countyDeterminationDao.getClientCounties(clientId);
    return counties.isEmpty() || counties.contains(staffCounty());
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
    return checkByAssignmentAccessType(accessType);
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
