package gov.ca.cwds.cans.domain.entity.facade;

import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public class Statistics {
  public static final String NQ_STAFF_ASSESSMENT_STATISTICS =
      "Statistics.getStaffAssessmentStatistics";
  public static final String NQ_PARAM_RACF_IDS = "racfIds";

  private String externalId;
  private long inProgressCount;
  private long submittedCount;
}
