package gov.ca.cwds.cans.domain.search;

import gov.ca.cwds.cans.domain.entity.Persistent;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public abstract class SearchResult<E extends Persistent> {
  private Collection<E> records = new ArrayList<>();
  private long totalRecords;
}
