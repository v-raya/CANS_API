package gov.ca.cwds.cans.domain.dto.logging;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import java.time.LocalDateTime;

/** @author denys.davydov */
public interface SubmitLoggable<T extends Dto> {
  LocalDateTime getSubmittedTimestamp();

  T setSubmittedTimestamp(LocalDateTime timestamp);

  PersonDto getSubmittedBy();

  T setSubmittedBy(PersonDto user);
}
