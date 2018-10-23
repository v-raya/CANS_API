package gov.ca.cwds.cans.domain.dto.logging;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import java.time.LocalDateTime;

/** @author denys.davydov */
public interface CompleteLoggable<T extends Dto> {
  LocalDateTime getCompletedTimestamp();

  T setCompletedTimestamp(LocalDateTime timestamp);

  PersonDto getCompletedBy();

  T setCompletedBy(PersonDto user);
}
