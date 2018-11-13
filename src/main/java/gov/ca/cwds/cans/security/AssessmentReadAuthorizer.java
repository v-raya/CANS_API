package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import java.util.Collection;
import java.util.stream.Collectors;

public class AssessmentReadAuthorizer extends BaseAuthorizer<Assessment, Long> {

  @Inject
  private ClientReadAuthorizer clientReadAuthorizer;

  @Inject
  private AssessmentDao assessmentDao;

  protected boolean checkId(Long id) {
    Assessment assessment = assessmentDao.find(id);
    return checkInstance(assessment);
  }

  protected boolean checkInstance(Assessment assessment) {
    return clientReadAuthorizer.checkId(assessment.getPerson().getExternalId());
  }

  @Override
  protected Collection<Assessment> filterInstances(Collection<Assessment> instances) {
    Collection<String> clientIds =
        instances.stream().map(assessment -> assessment.getPerson().getExternalId()).collect(
            Collectors.toSet());
    Collection<String> filterIds = clientReadAuthorizer.filterIds(clientIds);
    return instances.stream()
        .filter(assessment -> filterIds.contains(assessment.getPerson().getExternalId())).collect(
            Collectors.toList());
  }

  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
