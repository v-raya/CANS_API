package gov.ca.cwds.cans.domain.dto.person;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.enumeration.Gender;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.Race;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.validation.ValidPerson;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ValidPerson
public class PersonShortDto extends Dto {

  @NotNull private PersonRole personRole;

  @NotEmpty
  @Size(min = 1, max = 20)
  private String firstName;

  @Size(max = 20)
  private String middleName;

  @NotEmpty
  @Size(min = 1, max = 25)
  private String lastName;

  @Size(max = 4)
  private String suffix;

  @Size(max = 10)
  private String identifier;

  @NotEmpty private String externalId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate dob;

  private Boolean estimatedDob;
  private Gender gender;
  private Race race;
  private String countyClientNumber;
  private String clientIndexNumber;
  private SensitivityType sensitivityType;
}
