package gov.ca.cwds.cans.domain.search;

import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public class SearchPersonPo implements SearchPo {

  private PersonRole personRole;
  private String externalId;
}
