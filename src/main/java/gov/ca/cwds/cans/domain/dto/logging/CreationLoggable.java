package gov.ca.cwds.cans.domain.dto.logging;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import java.time.LocalDateTime;

/** @author denys.davydov */
public interface CreationLoggable<T extends Dto> {
  LocalDateTime getCreatedTimestamp();

  T setCreatedTimestamp(LocalDateTime timestamp);

  PersonDto getCreatedBy();

  T setCreatedBy(PersonDto user);
}
