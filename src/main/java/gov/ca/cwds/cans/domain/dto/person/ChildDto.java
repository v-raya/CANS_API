package gov.ca.cwds.cans.domain.dto.person;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.validation.ValidPerson;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/** @author CWDS TPT-2 Team */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ValidPerson
public class ChildDto extends PersonDto {
  @Valid
  private List<CountyDto> counties = new ArrayList<>();

  @Override
  public PersonRole getPersonRole() {
    return PersonRole.CLIENT;
  }

  /**
   * Sets nothing because the PersonRole is determined already.
   * @deprecated
   * @param personRole
   * @return self
   */
  @Override
  @Deprecated
  public PersonShortDto setPersonRole(PersonRole personRole) {
    return this;
  }
}
