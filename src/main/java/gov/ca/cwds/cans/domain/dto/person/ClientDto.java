package gov.ca.cwds.cans.domain.dto.person;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.validation.ValidPerson;
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
public class ClientDto extends PersonDto {

  @Override
  public PersonRole getPersonRole() {
    return PersonRole.CLIENT;
  }

  /**
   * Sets nothing because the PersonRole is determined already.
   *
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
