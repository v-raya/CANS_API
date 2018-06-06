package gov.ca.cwds.cans.domain.dto;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;
import static gov.ca.cwds.rest.api.domain.DomainObject.TIMESTAMP_ISO8601_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.logging.CreationLoggable;
import gov.ca.cwds.cans.domain.dto.logging.SubmitLoggable;
import gov.ca.cwds.cans.domain.dto.logging.UpdateLoggable;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import java.time.LocalDate;
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
  private PersonDto person;
  private AssessmentType assessmentType;
  private AssessmentStatus status;
  private AssessmentJson state;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate eventDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime createdTimestamp;
  private PersonDto createdBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime updatedTimestamp;
  private PersonDto updatedBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime submittedTimestamp;
  private PersonDto submittedBy;
}
