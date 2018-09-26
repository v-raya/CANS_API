package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.PersonShortDto;
import gov.ca.cwds.cans.domain.entity.Person;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface PersonShortMapper
    extends AMapper<Person, PersonShortDto>, PersonAuthorizationMapping {}
