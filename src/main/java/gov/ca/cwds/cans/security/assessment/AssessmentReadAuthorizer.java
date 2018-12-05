package gov.ca.cwds.cans.security.assessment;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AssessmentReadAuthorizer extends AssessmentOperationAuthorizer {

  @Inject
  private ClientDao clientDao;

  public AssessmentReadAuthorizer() {
    super(AssessmentOperation.read);
  }

  protected Collection<Assessment> filterInstances(Collection<Assessment> instances) {
    return filterAccessibleInstances(instances).stream()
        .filter(assessment -> this.checkOperation(assessment, true))
        .collect(Collectors.toList());
  }

  private Collection<Assessment> filterAccessibleInstances(Collection<Assessment> instances) {
    Collection<String> clientIds = extractClientIds(instances);
    Collection<String> filterIds = filterClientIds(clientIds);
    return instances
        .stream()
        .filter(assessment -> filterIds.contains(assessment.getPerson().getExternalId()))
        .collect(Collectors.toList());
  }

  private Collection<String> extractClientIds(Collection<Assessment> instances) {
    return instances
        .stream()
        .map(assessment -> assessment.getPerson().getExternalId())
        .collect(Collectors.toSet());
  }

  private Collection<String> filterClientIds(Collection<String> ids) {
    Collection<String> filteredByAssignments = clientDao
        .filterClientIdsByAssignment(ids, staffId());
    Collection<String> filteredByAbstractAccess = clientAbstractReadAuthorizer
        .filterIds(ids);
    return mergeClientIds(ids, filteredByAssignments, filteredByAbstractAccess);
  }

  private Collection<String> mergeClientIds(
      Collection<String> ids,
      Collection<String> filteredByAssignments,
      Collection<String> filteredBySealedSensitive) {
    if (filteredByAssignments.size() != ids.size()) {
      Set<String> result = new HashSet<>(filteredByAssignments);
      result.addAll(filteredBySealedSensitive);
      return result;
    } else {
      return ids;
    }
  }

}
