package gov.ca.cwds.cans.domain.dto.assessment;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.SearchRequest;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchAssessmentRequest implements SearchRequest {
  private Long personId;
  private String clientIdentifier;
  private Boolean includeDeleted;
}
