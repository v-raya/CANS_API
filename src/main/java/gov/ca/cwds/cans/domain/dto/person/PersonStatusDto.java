package gov.ca.cwds.cans.domain.dto.person;

import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonStatusDto {

  private String externalId;
  private LocalDate eventDate;
  private LocalDateTime updatedTimestamp;
  private LocalDateTime submittedTimestamp;
  private AssessmentStatus status;
}
