package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import gov.ca.cwds.cans.domain.mapper.StaffStatisticMapper;
import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientCountByStaff;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import gov.ca.cwds.security.utils.PrincipalUtils;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StaffService {

  @Inject private StatisticsService statisticsService;
  @Inject private StaffPersonDao staffPersonDao;
  @Inject private StaffStatisticMapper staffStatisticMapper;

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

  @UnitOfWork(CMS)
  public Collection<ClientCountByStaff> getClientCountByStaffIds(
      final Collection<String> staffIds) {
    return staffPersonDao.countClientsByStaffIds(staffIds);
  }
}
