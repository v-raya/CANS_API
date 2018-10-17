package gov.ca.cwds.cans.domain.entity.facade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {
  public static final String NQ_STAFF_ASSESSMENT_STATISTICS =
      "Statistics.getStaffAssessmentStatistics";
  public static final String NQ_PARAM_RACF_IDS = "racfIds";

  private String externalId;
  private long inProgressCount;
  private long submittedCount;
}
