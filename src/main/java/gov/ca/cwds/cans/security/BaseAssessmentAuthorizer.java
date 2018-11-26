package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAssessmentAuthorizer extends BaseAuthorizer<Assessment, Long> {

  private static final Logger LOG = LoggerFactory.getLogger(BaseAssessmentAuthorizer.class);

  @Inject private AssessmentDao assessmentDao;

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
}
