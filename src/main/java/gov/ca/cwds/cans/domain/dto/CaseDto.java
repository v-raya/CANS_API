package gov.ca.cwds.cans.domain.dto;

import static gov.ca.cwds.rest.api.domain.DomainObject.TIMESTAMP_ISO8601_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.logging.CreationLoggable;
import java.time.LocalDateTime;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseDto extends Dto implements CreationLoggable<CaseDto> {
  @NotEmpty
  @Pattern(regexp = "^\\d{4}-\\d{3}-\\d{4}-\\d{8}$")
  private String externalId;

  private PersonDto createdBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime createdTimestamp;
}
