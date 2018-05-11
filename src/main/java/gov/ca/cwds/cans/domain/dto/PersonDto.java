package gov.ca.cwds.cans.domain.dto;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.Gender;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.Race;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PersonDto extends Dto {
  private PersonRole personRole;
  private String firstName;
  private String lastName;
  private String externalId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate dob;

  private Boolean estimatedDob;
  private Gender gender;
  private Race race;
  private String countyClientNumber;
  private String clientIndexNumber;
  private CountyDto county;
}
