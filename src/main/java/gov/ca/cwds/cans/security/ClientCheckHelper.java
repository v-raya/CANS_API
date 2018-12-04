package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.data.dao.cms.CountyDeterminationDao;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CWDS TPT-2 Team
 */
@Slf4j
public class ClientCheckHelper {

  @Inject
  private CansClientAbstractReadAuthorizer clientAbstractReadAuthorizer;
  @Inject
  private CountyDeterminationDao countyDeterminationDao;
  @Inject
  private ClientReadAuthorizer clientReadAuthorizer;

  public boolean checkWriteAssessmentByClientId(String clientId) {
    return checkAssessmentByClientIdAndAccessType(clientId, AccessType.RW);
  }

  public boolean checkReadAssessmentByClientId(String clientId) {
    return checkAssessmentByClientIdForAnyAccessTypes(clientId);
  }

  private boolean checkAssessmentByClientIdForAnyAccessTypes(String clientId) {
    return checkAssessmentByClientIdAndAccessType(clientId, null);
  }

  private boolean checkAssessmentByClientIdAndAccessType(
      String clientId, AccessType desiredAccessType) {
    return (checkByCounty(clientId) && checkClientAbstractAccess(clientId))
        || checkByAssignment(clientId, desiredAccessType)
        || checkBySubordinateAssignment(clientId, desiredAccessType);
  }

  private boolean checkClientAbstractAccess(String clientId) {
    boolean isClientAbstractAuthorized = clientAbstractReadAuthorizer.checkClientId(clientId);
    log.info(
        "Authorization: client [{}] abstract authorization result [{}]",
        clientId,
        isClientAbstractAuthorized);
    return isClientAbstractAuthorized;
  }

  private boolean checkByCounty(String clientId) {
    Collection<Short> counties = countyDeterminationDao.getClientCounties(clientId);
    boolean hasEmptyOrSameCounty = counties.isEmpty() || counties.contains(staffCounty());
    log.info(
        "Authorization: client [{}] has no or the same county [{}] result [{}]",
        clientId,
        counties,
        hasEmptyOrSameCounty);
    return hasEmptyOrSameCounty;
  }

  public Short staffCounty() {
    return Short.valueOf(getPrincipal().getCountyCwsCode());
  }

  protected PerryAccount getPrincipal() {
    return PrincipalUtils.getPrincipal();
  }

  private boolean checkByAssignment(String clientId, AccessType desiredAccessType) {
    boolean isAssignedToClient = checkByAccessType(desiredAccessType,
        clientReadAuthorizer.getAccessType(clientId));
    log.info(
        "Authorization: client [{}] assigned with [{}] check result [{}]",
        clientId,
        desiredAccessType == null ? "Any" : desiredAccessType,
        isAssignedToClient);
    return isAssignedToClient;
  }

  private boolean checkBySubordinateAssignment(String clientId, AccessType desiredAccessType) {
    boolean isAssignedToSubordinate = checkByAccessType(desiredAccessType,
        clientReadAuthorizer.getAccessTypeBySupervisor(clientId));
    log.info(
        "Authorization: client [{}] subordinates assignment with [{}] check result [{}]",
        clientId,
        desiredAccessType == null ? "Any" : desiredAccessType,
        isAssignedToSubordinate);
    return isAssignedToSubordinate;
  }

  private boolean checkByAccessType(AccessType desiredAccessType, AccessType actualAccessType) {
    return Optional.ofNullable(desiredAccessType)
        .map(at -> actualAccessType == at)
        .orElseGet(() -> actualAccessType != AccessType.NONE);
  }
}
