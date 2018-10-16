package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import gov.ca.cwds.cans.domain.mapper.StaffStatisticMapper;
import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
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
    final Collection<StaffBySupervisor> staffList =
        getStaffBySupervisor(PrincipalUtils.getPrincipal().getStaffId());
    final Set<String> staffRacfIds =
        staffList.stream().map(StaffBySupervisor::getRacfId).collect(Collectors.toSet());
    if (staffRacfIds.isEmpty()) {
      return Collections.emptyList();
    }
    final Map<String, Statistics> statisticsMap =
        statisticsService.getStaffStatistics(staffRacfIds);
    final Collection<StaffStatisticsDto> results = new ArrayList<>(staffRacfIds.size());
    for (StaffBySupervisor staff : staffList) {
      final Statistics statistics = statisticsMap.get(staff.getRacfId());
      final StaffStatisticsDto statisticsDto = staffStatisticMapper.toDto(staff, statistics);
      results.add(statisticsDto);
    }
    return results;
  }

  @UnitOfWork(CMS)
  public Collection<StaffBySupervisor> getStaffBySupervisor(final String supervisorId) {
    return staffPersonDao.findStaffBySupervisorId(supervisorId);
  }
}