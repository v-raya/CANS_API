package gov.ca.cwds.cans.domain.dto.assessment;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StartAssessmentRequest {
  private Long instrumentId;
  private Long personId;
  private Long cftId;
}
