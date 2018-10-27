package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus;
import gov.ca.cwds.cans.domain.mapper.StaffClientMapper;
import gov.ca.cwds.cans.domain.mapper.StaffPersonMapper;
import gov.ca.cwds.data.legacy.cms.dao.CaseDao;
import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import gov.ca.cwds.security.utils.PrincipalUtils;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StaffService {

  @Inject private StaffPersonDao staffPersonDao;
  @Inject private PersonService personService;
  @Inject private CaseDao caseDao;
  @Inject private StaffClientMapper staffClientMapper;
  @Inject private StaffPersonMapper staffPersonMapper;

  public Collection<StaffStatisticsDto> getStaffStatisticsBySupervisor() {
    final String currentStaffId = PrincipalUtils.getPrincipal().getStaffId();
    final Collection<StaffBySupervisor> staffList = getStaffBySupervisor(currentStaffId);
    if (staffList.isEmpty()) {
      return Collections.emptyList();
    }

    // fetch all clients ids from legacy
    final Map<String, StaffBySupervisor> staffByIdMap =
        staffList
            .stream()
            .collect(Collectors.toMap(StaffBySupervisor::getIdentifier, staff -> staff));
    final Map<String, Set<String>> clientIdsByStaffIds =
        fetchClientIdsByStaffIds(staffByIdMap.keySet());

    // fetch all clients statuses by their ids from postgres
    final Map<String, String> base62toBase10ClientIdsMap =
        clientIdsByStaffIds
            .values()
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toSet())
            .stream()
            .collect(Collectors.toMap(id -> id, CmsKeyIdGenerator::getUIIdentifierFromKey));

    final List<StaffClientDto> clientsStatuses =
        personService.findStatusesByExternalIds(new HashSet<>(base62toBase10ClientIdsMap.values()));
    final Map<String, ClientAssessmentStatus> clientStatusMap =
        clientsStatuses
            .stream()
            .collect(
                Collectors.toMap(
                    StaffClientDto::getExternalId,
                    StaffClientDto::getStatus,
                    (oldValue, value) -> value));
    //    final Map<String, ClientAssessmentStatus> clientStatusMap =
    //        statisticsService.fetchClientsStatuses(base62toBase10ClientIdsMap.values());

    // group clients with statuses by staff
    final Collection<StaffStatisticsDto> results = new ArrayList<>();
    clientIdsByStaffIds.forEach(
        (staffId, clients) -> {
          final StaffBySupervisor staff = staffByIdMap.get(staffId);
          final StaffStatisticsDto staffStatistics =
              new StaffStatisticsDto()
                  .setStaffPerson(staffPersonMapper.toStaffPersonDto(staff))
                  .setClientsCount(clients.size());
          clients.forEach(
              clientId -> {
                final String base62ClientId = base62toBase10ClientIdsMap.get(clientId);
                incrementStatisticByStatus(staffStatistics, clientStatusMap.get(base62ClientId));
              });
          results.add(staffStatistics);
        });
    return results;
  }

  private void incrementStatisticByStatus(
      final StaffStatisticsDto staffStatistics, final ClientAssessmentStatus clientStatus) {
    if (clientStatus == null) {
      staffStatistics.incrementNoPriorCansCount();
      return;
    }
    switch (clientStatus) {
      case IN_PROGRESS:
        staffStatistics.incrementInProgressCount();
        break;
      case COMPLETED:
        staffStatistics.incrementCompletedCount();
        break;
      default:
    }
  }

  @UnitOfWork(CMS)
  public Collection<StaffBySupervisor> getStaffBySupervisor(final String supervisorId) {
    return staffPersonDao.findStaffBySupervisorId(supervisorId);
  }

  public Collection<StaffClientDto> findAssignedPersonsForStaffId(String staffId) {
    Collection<ClientByStaff> clientByStaffs = findClientsByStaffId(staffId);
    if (clientByStaffs.isEmpty()) {
      return Collections.emptyList();
    }
    Map<String, ClientByStaff> clientsByStaffMap =
        clientByStaffs
            .stream()
            .collect(Collectors.toMap(ClientByStaff::getIdentifier, item -> item));
    List<StaffClientDto> statuses =
        personService.findStatusesByExternalIds(clientsByStaffMap.keySet());
    statuses.forEach(
        item -> staffClientMapper.map(clientsByStaffMap.get(item.getExternalId()), item));
    return statuses;
  }

  @UnitOfWork(CMS)
  Collection<ClientByStaff> findClientsByStaffId(String staffId) {
    return caseDao.findClientsByStaffIdAndActiveDate(staffId, LocalDate.now());
  }

  @UnitOfWork(CMS)
  public Map<String, Set<String>> fetchClientIdsByStaffIds(final Collection<String> staffIds) {
    return staffPersonDao.findClientIdsByStaffIds(staffIds);
  }
}
