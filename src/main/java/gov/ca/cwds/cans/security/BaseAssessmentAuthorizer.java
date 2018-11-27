package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.dao.cms.CountyDeterminationDao;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAssessmentAuthorizer extends BaseAuthorizer<Assessment, Long> {

  private static final Logger LOG = LoggerFactory.getLogger(BaseAssessmentAuthorizer.class);

  @Inject private AssessmentDao assessmentDao;

  @Inject private CansClientAbstractReadAuthorizer clientAbstractReadAuthorizer;

  @Inject private CountyDeterminationDao countyDeterminationDao;

  @Inject private ClientReadAuthorizer clientReadAuthorizer;

  @Override
  protected boolean checkId(Long id) {
    long startTime = System.currentTimeMillis();
    LOG.info("Authorization: assessment [{}] started", id);
    Assessment assessment = assessmentDao.find(id);
    boolean isAuthorized = checkInstance(assessment);
    LOG.info(
        "Authorization: assessment [{}] finished with result [{}] in {} ms",
        id,
        isAuthorized,
        System.currentTimeMillis() - startTime);
    return isAuthorized;
  }

  @Override
  protected boolean checkInstance(Assessment assessment) {
    String clientId = assessment.getPerson().getExternalId();
    return checkSealedSensitive(clientId)
        || checkByAssignment(clientId)
        || checkBySubordinateAssignment(clientId);
  }

  protected boolean checkSealedSensitive(String clientId) {
    boolean isSealedSensitive = checkByCounty(clientId) && checkClientAbstractAccess(clientId);
    LOG.info(
        "Authorization: client [{}] county and abstract result [{}]", clientId, isSealedSensitive);
    return isSealedSensitive;
  }

  private boolean checkClientAbstractAccess(String clientId) {
    boolean isClientAbstractAuthorized = clientAbstractReadAuthorizer.checkClientId(clientId);
    LOG.info(
        "Authorization: client [{}] abstract authorization result [{}]",
        clientId,
        isClientAbstractAuthorized);
    return isClientAbstractAuthorized;
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

  protected Short staffCounty() {
    return Short.valueOf(PrincipalUtils.getPrincipal().getCountyCwsCode());
  }

  protected String staffId() {
    return PrincipalUtils.getStaffPersonId();
  }

  protected boolean checkByAssignment(String clientId) {
    boolean isAssignedToClient = clientReadAuthorizer.getAccessType(clientId) == AccessType.RW;
    LOG.info(
        "Authorization: client [{}] assigned with RW check result [{}]",
        clientId,
        isAssignedToClient);
    return isAssignedToClient;
  }

  protected boolean checkBySubordinateAssignment(String clientId) {
    boolean isAssignedToSubordinate =
        clientReadAuthorizer.getAccessTypeBySupervisor(clientId) == AccessType.RW;
    LOG.info(
        "Authorization: client [{}] subordinates assignment with RW check result [{}]",
        clientId,
        isAssignedToSubordinate);
    return isAssignedToSubordinate;
  }
}
