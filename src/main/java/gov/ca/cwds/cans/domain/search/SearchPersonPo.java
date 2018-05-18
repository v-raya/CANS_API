package gov.ca.cwds.cans.domain.search;

import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import lombok.Data;

/** @author denys.davydov */
@Data
public class SearchPersonPo implements SearchPo {

  private PersonRole personRole;
}
