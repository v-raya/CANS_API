package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/** @author denys.davydov */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "class"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = DomainJson.class, name = "domain"),
  @JsonSubTypes.Type(value = DomainsGroupJson.class, name = "domainsGroup")
})
@Data
public class AbstractAssessmentChildJson implements Json {
  private static final long serialVersionUID = 6911725256021182092L;

  private Long id;
  private String code;
  private Boolean underSix;
  private Boolean aboveSix;

}
