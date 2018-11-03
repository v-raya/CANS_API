package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.StaffPersonDto;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.data.legacy.cms.entity.StaffPerson;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** @author denys.davydov */
@Mapper(uses = CountyMapper.class)
public interface StaffPersonMapper {
  StaffPersonDto toStaffPersonDto(StaffBySupervisor staffBySupervisor);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "entity.phoneNo", target = "phoneNumber")
  @Mapping(
      expression = "java(entity.getTelExtNo() == 0 ? null : entity.getTelExtNo().toString())",
      target = "phoneExtCode")
  @Mapping(source = "entity.emailAddr", target = "email")
  @Mapping(source = "countyEntity", target = "county")
  StaffPersonDto toStaffPersonDto(StaffPerson entity, County countyEntity);
}
