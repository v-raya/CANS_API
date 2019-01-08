package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/** @author denys.davydov */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class AssessmentJson implements Json {

  private static final long serialVersionUID = -6936125676164346410L;

  private Long id;
  private Boolean underSix;
  @Valid private DomainJson caregiverDomainTemplate;

  @Valid @NotNull @NotEmpty private List<DomainJson> domains = new ArrayList<>();
}
