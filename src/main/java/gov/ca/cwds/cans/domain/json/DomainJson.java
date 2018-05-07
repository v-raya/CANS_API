package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/** @author denys.davydov */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DomainJson implements Json {
  private Long id;
  private String name;
  private Boolean underSix;
  private Boolean forceDomainShow;
  private Set<QuestionJson> questions = new HashSet<>();
  private AssessmentJson assessment;
}
