package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.dao.cms.CountyDeterminationDao;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssessmentReadAuthorizer extends AssessmentWriteAuthorizer {

  @Inject private CountyDeterminationDao countyDeterminationDao;

  @Inject private CansClientAbstractReadAuthorizer clientAbstractReadAuthorizer;

  @Inject private ClientReadAuthorizer clientReadAuthorizer;

  @Inject private ClientDao clientDao;

  @Inject private ClientCheckHelper clientCheckHelper;

  @Override
  protected boolean checkAssessmentByClientId(String clientId) {
    return clientCheckHelper.checkReadAssessmentByClientId(clientId);
  }

  @Override
  protected Collection<Assessment> filterInstances(Collection<Assessment> instances) {
    Collection<String> clientIds =
        instances
            .stream()
            .map(assessment -> assessment.getPerson().getExternalId())
            .collect(Collectors.toSet());
    Collection<String> filterIds = filterClientIds(clientIds);
    return instances
        .stream()
        .filter(assessment -> filterIds.contains(assessment.getPerson().getExternalId()))
        .collect(Collectors.toList());
  }

  private Collection<String> filterClientIds(Collection<String> ids) {
    Collection<String> filteredByAssignments =
        clientDao.filterClientIdsByAssignment(ids, staffId());

    Collection<String> filteredBySealedSensitive = filterSealedSensitive(ids);
    return clientReadAuthorizer.mergeClientIds(
        ids, filteredByAssignments, filteredBySealedSensitive);
  }

  protected String staffId() {
    return PrincipalUtils.getStaffPersonId();
  }

  private Collection<String> filterSealedSensitive(Collection<String> ids) {
    Collection<String> filteredByAbstractRules =
        new HashSet<>(clientAbstractReadAuthorizer.filterClientIds(ids));
    Collection<String> filteredByCounties = filterByCounties(ids);
    filteredByAbstractRules.retainAll(filteredByCounties);
    return filteredByAbstractRules;
  }

  private Collection<String> filterByCounties(Collection<String> ids) {
    Map<String, List<Short>> countiesMap = countyDeterminationDao.getClientCountiesMap(ids);
    Short staffCounty = clientCheckHelper.staffCounty();
    Collection<String> result = new HashSet<>();
    countiesMap.forEach(
        (id, counties) -> {
          if (counties.isEmpty() || counties.contains(staffCounty)) {
            result.add(id);
          }
        });
    return result;
  }
}
