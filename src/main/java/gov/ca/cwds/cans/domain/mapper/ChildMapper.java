package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.ChildDto;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author CWDS TPT-2 Team
 */
@Mapper
public interface ChildMapper {


  @Mapping(source = "commonFirstName", target = "firstName")
  @Mapping(source = "commonLastName", target = "lastName")
  @Mapping(source = "commonMiddleName", target = "middleName")
  @Mapping(source = "birthDate", target = "dob")
  @Mapping(source = "identifier", target = "externalId")
  @Mapping(source = "c", target = "county")
  ChildDto toDto(Client client);
}
