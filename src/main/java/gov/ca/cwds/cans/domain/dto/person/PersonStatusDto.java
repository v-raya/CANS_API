package gov.ca.cwds.cans.domain.dto.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PersonStatusDto {

  @JsonIgnore private String externalId;
  private LocalDate eventDate;
  private LocalDateTime updatedTimestamp;
  private LocalDateTime submittedTimestamp;
  private AssessmentStatus status;
}
