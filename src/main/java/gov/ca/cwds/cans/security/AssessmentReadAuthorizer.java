package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import java.util.Collection;
import java.util.stream.Collectors;

public class AssessmentReadAuthorizer extends BaseAssessmentAuthorizer {

  @Inject private ClientReadAuthorizer clientReadAuthorizer;

  @Override
  protected boolean checkInstance(Assessment assessment) {
    return clientReadAuthorizer.checkId(assessment.getPerson().getExternalId());
  }

  @Override
  protected Collection<Assessment> filterInstances(Collection<Assessment> instances) {
    Collection<String> clientIds =
        instances
            .stream()
            .map(assessment -> assessment.getPerson().getExternalId())
            .collect(Collectors.toSet());
    Collection<String> filterIds = clientReadAuthorizer.filterIds(clientIds);
    return instances
        .stream()
        .filter(assessment -> filterIds.contains(assessment.getPerson().getExternalId()))
        .collect(Collectors.toList());
  }

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
