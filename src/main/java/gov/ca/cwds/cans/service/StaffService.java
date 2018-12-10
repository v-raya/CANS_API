package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.CountyDao;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.domain.mapper.StaffClientMapper;
import gov.ca.cwds.cans.domain.mapper.StaffPersonMapper;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.data.legacy.cms.dao.CaseDao;
import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
import gov.ca.cwds.data.legacy.cms.entity.StaffPerson;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import gov.ca.cwds.rest.exception.ExpectedException;
import gov.ca.cwds.security.utils.PrincipalUtils;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response.Status;

public class StaffService {

  @Inject private StaffPersonDao staffPersonDao;
  @Inject private PersonService personService;
  @Inject private CaseDao caseDao;
  @Inject private StaffClientMapper staffClientMapper;
  @Inject private StaffPersonMapper staffPersonMapper;
  @Inject private AssessmentService assessmentService;
  @Inject private AssessmentMapper assessmentMapper;
  @Inject private CountyDao countyDao;
  @Inject private SecurityService securityService;

  public Collection<StaffStatisticsDto> getStaffStatisticsBySupervisor() {
    final String currentStaffId = PrincipalUtils.getPrincipal().getStaffId();
    final Collection<StaffBySupervisor> staffList = getStaffBySupervisor(currentStaffId);
    if (staffList.isEmpty()) {
      return Collections.emptyList();
    }
    final Map<String, StaffBySupervisor> staffByIdMap =
        staffList
            .stream()
            .collect(
                Collectors.toMap(
                    StaffBySupervisor::getIdentifier, staff -> staff, (oldValue, value) -> value));
    final Map<String, Set<String>> clientIdsByStaffIds =
        fetchClientIdsByStaffIds(staffByIdMap.keySet());
    final Set<String> clientIds =
        clientIdsByStaffIds
            .values()
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    final Map<String, ClientAssessmentStatus> clientStatusMap = fetchClientToStatusMap(clientIds);
    return mergeResults(staffByIdMap, clientIdsByStaffIds, clientStatusMap);
  }

  private Collection<StaffStatisticsDto> mergeResults(
      Map<String, StaffBySupervisor> staffByIdMap,
      Map<String, Set<String>> clientIdsByStaffIds,
      Map<String, ClientAssessmentStatus> clientStatusMap) {
    final Collection<StaffStatisticsDto> results = new ArrayList<>();
    clientIdsByStaffIds.forEach(
        (staffId, clients) -> {
          final StaffBySupervisor staff = staffByIdMap.get(staffId);
          final StaffStatisticsDto staffStatistics =
              new StaffStatisticsDto()
                  .setStaffPerson(staffPersonMapper.toStaffPersonDto(staff))
                  .setClientsCount(clients.size());
          clients.forEach(
              clientId ->
                  incrementStatisticByStatus(staffStatistics, clientStatusMap.get(clientId)));
          results.add(staffStatistics);
        });
    return results;
  }

  private Map<String, ClientAssessmentStatus> fetchClientToStatusMap(final Set<String> clientIds) {
    final List<StaffClientDto> clientsStatuses = personService.findStatusesByExternalIds(clientIds);
    return clientsStatuses
        .stream()
        .collect(
            Collectors.toMap(
                StaffClientDto::getExternalId,
                StaffClientDto::getStatus,
                (oldValue, value) -> value));
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
      case NO_PRIOR_CANS:
        staffStatistics.incrementNoPriorCansCount();
        break;
      default:
    }
  }

  @UnitOfWork(CMS)
  public Collection<StaffBySupervisor> getStaffBySupervisor(final String supervisorId) {
    return staffPersonDao.findStaffBySupervisorId(supervisorId);
  }

  public Collection<StaffClientDto> findAssignedPersonsForStaffId(String staffId) {
    if (staffId.length() > 3) {
      throw new ExpectedException("Staff id must consist of 3 symbols", Status.BAD_REQUEST);
    }
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
    Map<String, StaffClientDto> statusesMap =
        statuses.stream().collect(Collectors.toMap(StaffClientDto::getExternalId, item -> item));
    return merge(clientByStaffs, statusesMap);
  }

  private List<StaffClientDto> merge(
      Collection<ClientByStaff> clientByStaffs, Map<String, StaffClientDto> statusesMap) {
    List<StaffClientDto> out = new ArrayList<>();

    clientByStaffs.forEach(
        item -> {
          StaffClientDto staffClientDto = statusesMap.get(item.getIdentifier());
          if (staffClientDto == null) {
            staffClientDto = new StaffClientDto();
            staffClientDto.setStatus(ClientAssessmentStatus.NO_PRIOR_CANS);
          }
          staffClientMapper.map(item, staffClientDto);
          out.add(staffClientDto);
        });
    return out;
  }

  @UnitOfWork(CMS)
  Collection<ClientByStaff> findClientsByStaffId(String staffId) {
    securityService.checkPermission("staff:read:" + staffId);
    return caseDao.findClientsByStaffIdAndActiveDate(staffId, LocalDate.now());
  }

  @UnitOfWork(CMS)
  public Map<String, Set<String>> fetchClientIdsByStaffIds(final Collection<String> staffIds) {
    return staffPersonDao.findClientIdsByStaffIds(staffIds, LocalDate.now());
  }

  public Collection<AssessmentMetaDto> findAssessmentsByCurrentUser() {
    final Collection<Assessment> entities = assessmentService.getAssessmentsByCurrentUser();
    return assessmentMapper.toShortDtos(entities);
  }

  public StaffStatisticsDto getStaffPersonWithStatistics(final String staffId) {
    Require.requireNotNullAndNotEmpty(staffId);
    final StaffPerson entity = fetchLegacyStaffPerson(staffId);
    if (entity == null) {
      return null;
    }
    final County county = fetchCountyById(Long.valueOf(entity.getCntySpfcd(), 10));
    final Collection<StaffClientDto> assignedPeople = findAssignedPersonsForStaffId(staffId);
    return toStaffStatisticsDto(entity, county, assignedPeople);
  }

  private StaffStatisticsDto toStaffStatisticsDto(
      StaffPerson entity, County county, Collection<StaffClientDto> assignedPeople) {
    final StaffStatisticsDto result =
        new StaffStatisticsDto()
            .setStaffPerson(staffPersonMapper.toStaffPersonDto(entity, county))
            .setClientsCount(assignedPeople.size());
    assignedPeople.forEach(
        staffClient -> incrementStatisticByStatus(result, staffClient.getStatus()));
    return result;
  }

  @UnitOfWork(CMS)
  public StaffPerson fetchLegacyStaffPerson(final String staffId) {
    return staffPersonDao.find(staffId);
  }

  private County fetchCountyById(final Long id) {
    return countyDao.find(id);
  }
}
