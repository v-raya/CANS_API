package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.dao.cms.CountyDeterminationDao;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentReadAuthorizer extends AssessmentWriteAuthorizer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentReadAuthorizer.class);

  @Inject private CountyDeterminationDao countyDeterminationDao;

  @Inject private CansClientAbstractReadAuthorizer clientAbstractReadAuthorizer;

  @Inject private ClientReadAuthorizer clientReadAuthorizer;

  @Inject private ClientDao clientDao;

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

  private Collection<String> filterSealedSensitive(Collection<String> ids) {
    Collection<String> filteredByAbstractRules =
        new HashSet<>(clientAbstractReadAuthorizer.filterClientIds(ids));
    Collection<String> filteredByCounties = filterByCounties(ids);
    filteredByAbstractRules.retainAll(filteredByCounties);
    return filteredByAbstractRules;
  }

  private Collection<String> filterByCounties(Collection<String> ids) {
    Map<String, List<Short>> countiesMap = countyDeterminationDao.getClientCountiesMap(ids);
    Short staffCounty = staffCounty();
    Collection<String> result = new HashSet<>();
    countiesMap.forEach(
        (id, counties) -> {
          if (counties.isEmpty() || counties.contains(staffCounty)) {
            result.add(id);
          }
        });
    return result;
  }

  @Override
  protected boolean checkByAssignment(String clientId) {
    boolean isAssignedToClient = clientReadAuthorizer.getAccessType(clientId) != AccessType.NONE;
    LOG.info(
        "Authorization: client [{}] assigned with RW check result [{}]",
        clientId,
        isAssignedToClient);
    return isAssignedToClient;
  }

  @Override
  protected boolean checkBySubordinateAssignment(String clientId) {
    AccessType accessType = clientReadAuthorizer.getAccessTypeBySupervisor(clientId);
    boolean isAssignedToSubordinate = accessType != AccessType.NONE;
    LOG.info(
        "Authorization: client [{}] subordinates assignment with access type [{}] check result [{}]",
        clientId,
        accessType,
        isAssignedToSubordinate);
    return isAssignedToSubordinate;
  }
}
