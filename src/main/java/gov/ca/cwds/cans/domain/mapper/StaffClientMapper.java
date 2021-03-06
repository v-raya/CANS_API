package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface StaffClientMapper {
  @Mapping(source = "nameSuffix", target = "suffix")
  @Mapping(source = "birthDate", target = "dob")
  @Mapping(target = "sensitivityType", ignore = true)
  void map(ClientByStaff source, @MappingTarget StaffClientDto target);
}
