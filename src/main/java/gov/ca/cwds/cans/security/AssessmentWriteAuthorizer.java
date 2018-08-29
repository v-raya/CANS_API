package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.drools.DroolsConfiguration;

public class AssessmentWriteAuthorizer extends DroolsAuthorizer<Assessment, Long> {

  private static final String CONFIGURATION_NAME = "assessment-authorization-rules";
  @Inject
  private AssessmentDao assessmentDao;

  public AssessmentWriteAuthorizer() {
    super(new DroolsConfiguration<>(
        CONFIGURATION_NAME,
        CONFIGURATION_NAME,
        CONFIGURATION_NAME));
  }

  protected boolean checkId(Long id) {
    Assessment assessment = assessmentDao.find(id);
    return checkInstance(assessment);
  }

  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
