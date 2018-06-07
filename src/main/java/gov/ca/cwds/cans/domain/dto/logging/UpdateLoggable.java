package gov.ca.cwds.cans.domain.dto.logging;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import java.time.LocalDateTime;

/**
 * @author denys.davydov
 */
public interface UpdateLoggable<T extends Dto> {
  LocalDateTime getUpdatedTimestamp();
  T setUpdatedTimestamp(LocalDateTime timestamp);
  PersonDto getUpdatedBy();
  T setUpdatedBy(PersonDto user);
}
