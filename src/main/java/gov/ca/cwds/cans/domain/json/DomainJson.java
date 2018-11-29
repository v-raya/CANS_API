package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.RatingType;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @author denys.davydov */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class DomainJson implements Json {
  private static final long serialVersionUID = -7610361544786543959L;

  private Long id;
  private String code;
  private Boolean underSix;
  private Boolean aboveSix;
  private RatingType ratingType;
  private Boolean isCaregiverDomain;
  private String caregiverIndex;

  @Size(max = 2500)
  private String comment;

  @Size(max = 50)
  private String caregiverName;

  @Valid private List<ItemJson> items = new ArrayList<>();
}
