package gov.ca.cwds.cans.domain.mapper;

import static gov.ca.cwds.cans.Constants.EDITABLE;

import gov.ca.cwds.cans.domain.dto.person.PersonShortDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.security.annotations.Authorize;
import org.apache.shiro.authz.AuthorizationException;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * @author denys.davydov
 */
public interface PersonAuthorizationMapping {
  @AfterMapping
  default void refinePerson(Person person, @MappingTarget PersonShortDto personShort) {
    try {
      authorizedPersonMapping(personShort, person);
    } catch (AuthorizationException e) {
      unauthorizedPersonMapping(personShort);
    }
  }

  default void authorizedPersonMapping(
      PersonShortDto personShort, @Authorize("person:write:person") Person person) {
    personShort.getMetadata().put(EDITABLE, true);
  }

  default void unauthorizedPersonMapping(PersonShortDto personShort) {
    personShort.getMetadata().put(EDITABLE, false);
    personShort.setExternalId(null);
  }
}
