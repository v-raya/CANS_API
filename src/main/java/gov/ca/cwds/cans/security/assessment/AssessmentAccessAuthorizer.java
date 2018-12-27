package gov.ca.cwds.cans.security.assessment;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.security.ClientReadAuthorizer;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.rest.exception.ExpectedException;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.utils.PrincipalUtils;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AssessmentAccessAuthorizer extends BaseAuthorizer<Assessment, Long> {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentAccessAuthorizer.class);

  @Inject protected CansClientAbstractReadAuthorizer clientAbstractReadAuthorizer;
  @Inject private AssessmentDao assessmentDao;
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
    if (assessment == null
        || assessment.getPerson() == null
        || assessment.getPerson().getExternalId() == null) {
      throw new ExpectedException(
          "Assessment was not found in the database or has no person object", Status.NOT_FOUND);
    }
    String clientId = assessment.getPerson().getExternalId();
    return checkClientAbstractAccess(clientId)
        || checkByAssignment(clientId)
        || checkBySubordinateAssignment(clientId);
  }

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }

  protected String staffId() {
    return PrincipalUtils.getStaffPersonId();
  }

  private boolean checkClientAbstractAccess(String clientId) {
    boolean isClientAbstractAuthorized = clientAbstractReadAuthorizer.checkId(clientId);
    LOG.info(
        "Authorization: client [{}] abstract authorization result [{}]",
        clientId,
        isClientAbstractAuthorized);
    return isClientAbstractAuthorized;
  }

  private boolean checkByAssignment(String clientId) {
    boolean isAssignedToClient = clientReadAuthorizer.getAccessType(clientId) != AccessType.NONE;
    LOG.info(
        "Authorization: client [{}] assigned with RW check result [{}]",
        clientId,
        isAssignedToClient);
    return isAssignedToClient;
  }

  private boolean checkBySubordinateAssignment(String clientId) {
    boolean isAssignedToSubordinate =
        clientReadAuthorizer.getAccessTypeBySupervisor(clientId) != AccessType.NONE;
    LOG.info(
        "Authorization: client [{}] subordinates assignment check result [{}]",
        clientId,
        isAssignedToSubordinate);
    return isAssignedToSubordinate;
  }
}
