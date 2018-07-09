package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** @author denys.davydov */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class AssessmentJson implements Json {
  private static final long serialVersionUID = -6936125676164346410L;

  private Long id;
  private Boolean underSix;
  private DomainJson caregiverDomainTemplate;
  private List<DomainJson> domains = new ArrayList<>();
}
