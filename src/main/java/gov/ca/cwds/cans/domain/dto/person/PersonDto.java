package gov.ca.cwds.cans.domain.dto.person;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import gov.ca.cwds.cans.validation.ValidPerson;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ValidPerson
public class PersonDto extends PersonShortDto {
  @NotNull private CountyDto county;
  private String serviceSourceId;
  private String serviceSourceUiId;
  private ServiceSource serviceSource;
}
