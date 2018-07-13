package gov.ca.cwds.cans.domain.dto.assessment;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import gov.ca.cwds.cans.validation.ValidAssessment;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ValidAssessment
public class AssessmentDto extends AbstractAssessmentDto {
  @NotNull
  @Valid
  private AssessmentJson state;
}
