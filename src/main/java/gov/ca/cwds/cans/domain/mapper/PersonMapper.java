package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.security.annotations.Authorize;
import org.apache.shiro.authz.AuthorizationException;
import org.mapstruct.AfterMapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/** @author denys.davydov */
@Mapper(uses = CountyMapper.class)
public abstract class PersonMapper implements AMapper<Person, PersonDto> {

  private static final String EDITABLE = "editable";

  @AfterMapping
  protected void refinePersonDto(Person person, @MappingTarget PersonDto personDto) {
    try {
      authorizedPersonMapping(personDto, person);
    } catch (AuthorizationException e) {
      unauthorizedPersonMapping(personDto);
    }
  }

  protected void authorizedPersonMapping(PersonDto personDto, @Authorize("person:write:person") Person person) {
    personDto.getMetadata().put(EDITABLE, true);
  }

  protected void unauthorizedPersonMapping(PersonDto personDto) {
    personDto.getMetadata().put(EDITABLE, false);
    personDto.setCases(null);
    personDto.setExternalId(null);
  }
}
