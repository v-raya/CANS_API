package gov.ca.cwds.cans.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.logging.CreationLoggable;
import gov.ca.cwds.cans.domain.dto.logging.SubmitLoggable;
import gov.ca.cwds.cans.domain.dto.logging.UpdateLoggable;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AssessmentDto extends Dto
    implements CreationLoggable<AssessmentDto>,
        UpdateLoggable<AssessmentDto>,
        SubmitLoggable<AssessmentDto> {

  private Long instrumentId;
  private AssessmentType assessmentType;
  private AssessmentStatus status;
  private AssessmentJson state;

  private LocalDateTime createdTimestamp;
  private PersonDto createdBy;
  private LocalDateTime updatedTimestamp;
  private PersonDto updatedBy;
  private LocalDateTime submittedTimestamp;
  private PersonDto submittedBy;
}
