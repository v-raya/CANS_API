package gov.ca.cwds.cans.domain.dto.assessment;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;
import static gov.ca.cwds.rest.api.domain.DomainObject.TIMESTAMP_ISO8601_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.logging.CompleteLoggable;
import gov.ca.cwds.cans.domain.dto.logging.CreationLoggable;
import gov.ca.cwds.cans.domain.dto.logging.UpdateLoggable;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import gov.ca.cwds.cans.domain.enumeration.CompletedAs;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAssessmentDto extends Dto
    implements CreationLoggable<AbstractAssessmentDto>,
        UpdateLoggable<AbstractAssessmentDto>,
        CompleteLoggable<AbstractAssessmentDto> {
  private Long instrumentId;
  @NotNull private ClientDto person;
  private CountyDto county;
  private AssessmentType assessmentType;
  private String conductedBy;
  private AssessmentStatus status;
  private CompletedAs completedAs;
  private Boolean canReleaseConfidentialInfo;
  private Boolean hasCaregiver;

  @Size(min = 10, max = 10)
  private String serviceSourceId;

  private String serviceSourceUiId;

  private ServiceSource serviceSource;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate eventDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime createdTimestamp;

  private PersonDto createdBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime updatedTimestamp;

  private PersonDto updatedBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime completedTimestamp;

  private PersonDto completedBy;
}
