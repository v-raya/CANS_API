package gov.ca.cwds.cans.domain.search;

import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public class Pagination implements SearchParameters {
  private int page;
  private int pageSize;
}
