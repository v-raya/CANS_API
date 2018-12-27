package gov.ca.cwds.cans.domain.search;

import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public class SearchAssessmentParameters implements SearchParameters {
  private Long personId;
  private String clientIdentifier;
  private Long createdById;
  private Boolean includeDeleted = Boolean.FALSE;
}
