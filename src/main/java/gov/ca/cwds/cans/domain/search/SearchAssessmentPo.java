package gov.ca.cwds.cans.domain.search;

import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public class SearchAssessmentPo implements SearchPo {
  private Long personId;
  private Long createdById;
}
