package gov.ca.cwds.cans.domain.search;

import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public class SearchPersonParameters implements SearchParameters {
  private PersonRole personRole;
  private String externalId;
  private String usersCountyExternalId;
  private String firstName;
  private String middleName;
  private String lastName;
  private LocalDate dob;
  private Pagination pagination;
}
