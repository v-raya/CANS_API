package gov.ca.cwds.cans.domain.dto;

import static gov.ca.cwds.cans.Constants.ValidationMessages.ALPHA_SYMBOLS;
import static gov.ca.cwds.cans.Constants.ValidationMessages.ALPHA_SYMBOLS_WITH_PERIOD;
import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.Gender;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.Race;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class PersonDto extends Dto {
  @NotNull private PersonRole personRole;

  @NotEmpty
  @Size(min = 1, max = 50)
  @Pattern(regexp = "^[\\p{Alpha}]*$", message = ALPHA_SYMBOLS)
  private String firstName;

  @Size(max = 50)
  @Pattern(regexp = "^[\\p{Alpha}]*$", message = ALPHA_SYMBOLS)
  private String middleName;

  @NotEmpty
  @Size(min = 1, max = 50)
  @Pattern(regexp = "^[\\p{Alpha}]*$", message = ALPHA_SYMBOLS)
  private String lastName;

  @Size(max = 10)
  @Pattern(regexp = "^([\\p{Alpha}]*)|([\\p{Alpha}]+\\.)$", message = ALPHA_SYMBOLS_WITH_PERIOD)
  private String suffix;

  @NotEmpty
  @Pattern(regexp = "^\\d{4}-\\d{4}-\\d{4}-\\d{7}$")
  private String externalId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate dob;

  private Boolean estimatedDob;
  private Gender gender;
  private Race race;
  private String countyClientNumber;
  private String clientIndexNumber;
  @NotNull private CountyDto county;

  @Valid
  private List<CaseDto> cases = new ArrayList<>();
}
