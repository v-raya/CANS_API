package gov.ca.cwds.cans.security.assessment.facts;

import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.security.assessment.AssessmentOperation;
import gov.ca.cwds.security.realm.PerryAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssessmentOperationFact {

  private AssessmentOperation operation;
  private Assessment assessment;
  private PerryAccount user;
  private boolean assessmentAccessible;
}
