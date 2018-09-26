package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.entity.Person;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = CountyMapper.class)
public interface PersonMapper extends AMapper<Person, PersonDto>, PersonAuthorizationMapping {}
