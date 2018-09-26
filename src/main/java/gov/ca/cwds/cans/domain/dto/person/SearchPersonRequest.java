package gov.ca.cwds.cans.domain.dto.person;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.PaginationDto;
import gov.ca.cwds.cans.domain.dto.SearchRequest;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchPersonRequest implements SearchRequest {
  private PersonRole personRole;
  private String externalId;
  private String firstName;
  private String middleName;
  private String lastName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate dob;

  @NotNull @Valid
  private PaginationDto pagination;
}
