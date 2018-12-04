package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentWriteAuthorizer extends BaseAuthorizer<Assessment, Long> {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentWriteAuthorizer.class);

  @Inject private AssessmentDao assessmentDao;

  @Inject private ClientCheckHelper clientCheckHelper;

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
    long startTime = System.currentTimeMillis();
    Long assessmentId = assessment.getId();
    LOG.info("Authorization: assessment [{}] started", assessmentId);
    String clientId = assessment.getPerson().getExternalId();
    boolean isAuthorized = checkAssessmentByClientId(clientId);
    LOG.info(
        "Authorization: assessment [{}] finished with result [{}] in {} ms",
        assessmentId,
        isAuthorized,
        System.currentTimeMillis() - startTime);
    return isAuthorized;
  }

  protected boolean checkAssessmentByClientId(String clientId) {
    return clientCheckHelper.checkWriteAssessmentByClientId(clientId);
  }

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
