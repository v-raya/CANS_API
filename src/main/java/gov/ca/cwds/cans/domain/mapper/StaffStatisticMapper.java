package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** @author denys.davydov */
@Mapper
public interface StaffStatisticMapper {
  @Mapping(target = "staffPerson", source = "staff")
  StaffStatisticsDto toDto(StaffBySupervisor staff, Statistics statistics);
}
