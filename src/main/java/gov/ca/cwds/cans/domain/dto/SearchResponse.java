package gov.ca.cwds.cans.domain.dto;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public abstract class SearchResponse<D extends Dto> {
  private Collection<D> records = new ArrayList<>();
  private long totalRecords;
}
