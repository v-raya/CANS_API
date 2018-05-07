package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.AssessmentType;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** @author denys.davydov */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AssessmentJson implements Json {
  private Long id;
  private AssessmentType assessmentType;
  private AssessmentStatus status;
  private List<DomainJson> domains = new ArrayList<>();
}
