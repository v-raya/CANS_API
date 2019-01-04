package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.RatingType;
import gov.ca.cwds.cans.validation.ValidItemJson;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/** @author denys.davydov */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
@ValidItemJson
public class ItemJson implements Json {
  private static final long serialVersionUID = -4393123520723907456L;

  private String underSixId;
  private String aboveSixId;
  @NotNull @NotEmpty private String code;
  private Boolean required;
  private Boolean confidential;
  private Boolean confidentialByDefault;
  @NotNull private RatingType ratingType;
  private Boolean hasNaOption;
  private Integer rating;

  @Size(max = 250)
  private String comment;
}
