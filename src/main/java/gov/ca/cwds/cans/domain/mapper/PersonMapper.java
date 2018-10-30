package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.entity.Person;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/** @author denys.davydov */
@Mapper(uses = CountyMapper.class)
public interface PersonMapper extends AMapper<Person, PersonDto>, PersonAuthorizationMapping {
  @AfterMapping
  default void afterMapping(@MappingTarget Person person, ClientDto clientDto) {
    person.setExternalId(clientDto.getIdentifier());
  }
}
