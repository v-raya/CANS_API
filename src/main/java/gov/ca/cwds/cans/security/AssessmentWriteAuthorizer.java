package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.drools.DroolsConfiguration;

public class AssessmentWriteAuthorizer extends DroolsAuthorizer<Assessment, Long> {

  private static final String CONFIGURATION_NAME = "authorization-rules";
  private static final String AGENDA_GROUP_NAME = "assessment-read-authorization-rules";
  @Inject
  private AssessmentDao assessmentDao;

  public AssessmentWriteAuthorizer() {
    super(new DroolsConfiguration<>(
        CONFIGURATION_NAME,
        AGENDA_GROUP_NAME,
        CONFIGURATION_NAME));
  }

  @Override
  protected boolean checkId(Long id) {
    Assessment assessment = assessmentDao.find(id);
    return checkInstance(assessment);
  }

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
