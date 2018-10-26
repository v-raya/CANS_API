package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface StaffClientMapper {
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "middleName", target = "middleName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "nameSuffix", target = "suffix")
  @Mapping(source = "birthDate", target = "dob")
  @Mapping(source = "identifier", target = "externalId")
  @Mapping(target = "sensitivityType", ignore = true)
  void map(ClientByStaff source, @MappingTarget StaffClientDto target);
}
