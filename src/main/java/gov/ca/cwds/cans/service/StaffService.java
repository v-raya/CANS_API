package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import gov.ca.cwds.cans.domain.mapper.StaffClientMapper;
import gov.ca.cwds.cans.domain.mapper.StaffStatisticMapper;
import gov.ca.cwds.data.legacy.cms.dao.CaseDao;
import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientCountByStaff;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
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

public class StaffService {

  @Inject
  private StatisticsService statisticsService;
  @Inject
  private StaffPersonDao staffPersonDao;
  @Inject
  private PersonService personService;
  @Inject
  private StaffStatisticMapper staffStatisticMapper;
  @Inject
  private CaseDao caseDao;
  @Inject
  private StaffClientMapper staffClientMapper;

  public Collection<StaffStatisticsDto> getStaffStatisticsBySupervisor() {
    final String currentStaffId = PrincipalUtils.getPrincipal().getStaffId();
    final Collection<StaffBySupervisor> staffList = getStaffBySupervisor(currentStaffId);
    if (staffList.isEmpty()) {
      return Collections.emptyList();
    }

    final Map<String, ClientCountByStaff> clientCountByStaffMap =
        fetchClientCountByStaffMap(staffList);

    final Set<String> staffRacfIds =
        staffList.stream().map(StaffBySupervisor::getRacfId).collect(Collectors.toSet());
    final Map<String, Statistics> statisticsMap =
        statisticsService.getStaffStatistics(staffRacfIds);

    final Collection<StaffStatisticsDto> results = new ArrayList<>(staffRacfIds.size());
    for (StaffBySupervisor staff : staffList) {
      final Statistics statistics = statisticsMap.get(staff.getRacfId());
      final ClientCountByStaff clientCount = clientCountByStaffMap.get(staff.getIdentifier());
      final StaffStatisticsDto statisticsDto =
          staffStatisticMapper.toDto(staff, statistics, clientCount);
      results.add(statisticsDto);
    }
    return results;
  }

  private Map<String, ClientCountByStaff> fetchClientCountByStaffMap(
      Collection<StaffBySupervisor> staffList) {
    final Set<String> staffIds =
        staffList.stream().map(StaffBySupervisor::getIdentifier).collect(Collectors.toSet());
    final Collection<ClientCountByStaff> clientCounts = getClientCountByStaffIds(staffIds);
    final Map<String, ClientCountByStaff> resultMap =
        clientCounts
            .stream()
            .collect(Collectors.toMap(ClientCountByStaff::getIdentifier, dto -> dto));
    // add default empty values
    staffIds
        .stream()
        .filter(id -> !resultMap.containsKey(id))
        .forEach(id -> resultMap.put(id, new ClientCountByStaff(id, 0, 0)));
    return resultMap;
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
    return statuses
        .stream()
        .peek(item -> staffClientMapper.map(clientsByStaffMap.get(item.getExternalId()), item))
        .collect(Collectors.toList());
  }

  @UnitOfWork(CMS)
  Collection<ClientByStaff> findClientsByStaffId(String staffId) {
    return caseDao.findClientsByStaffIdAndActiveDate(staffId, LocalDate.now());
  }

  @UnitOfWork(CMS)
  public Collection<ClientCountByStaff> getClientCountByStaffIds(
      final Collection<String> staffIds) {
    return staffPersonDao.countClientsByStaffIds(staffIds);
  }
}
