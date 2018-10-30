package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.StaffPersonDto;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface StaffPersonMapper {
  StaffPersonDto toStaffPersonDto(StaffBySupervisor staffBySupervisor);
}
