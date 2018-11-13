package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;

public class AssessmentWriteAuthorizer extends BaseAuthorizer<Assessment, Long> {

  @Inject
  private ClientReadAuthorizer clientReadAuthorizer;

  @Inject
  private AssessmentDao assessmentDao;

  protected boolean checkId(Long id) {
    Assessment assessment = assessmentDao.find(id);
    return checkInstance(assessment);
  }

  protected boolean checkInstance(Assessment assessment) {
    String clientId = assessment.getPerson().getExternalId();
    return clientReadAuthorizer.checkClientAbstractAccess(clientId) || checkByAssignment(clientId);
  }

  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }

  private boolean checkByAssignment(String clientId) {
    return clientReadAuthorizer.getAccessType(clientId).equals(AccessType.RW);
  }
}
