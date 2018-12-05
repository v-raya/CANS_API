package gov.ca.cwds.cans.security.assessment;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.security.assessment.facts.AssessmentOperationFact;
import gov.ca.cwds.cans.service.CansRulesService;
import gov.ca.cwds.security.utils.PrincipalUtils;

public abstract class AssessmentOperationAuthorizer extends AssessmentAccessAuthorizer {

  private final AssessmentOperation operation;
  @Inject
  private CansRulesService rulesService;

  AssessmentOperationAuthorizer(AssessmentOperation operation) {
    this.operation = operation;
  }

  protected boolean checkInstance(Assessment assessment) {
    boolean isAssessmentAccessible = super.checkInstance(assessment);
    return checkOperation(assessment, isAssessmentAccessible);
  }


  boolean checkOperation(Assessment assessment, boolean isAssessmentAccessible) {
    AssessmentOperationFact operationFact = new AssessmentOperationFact(operation, assessment,
        PrincipalUtils.getPrincipal(), isAssessmentAccessible);
    return rulesService.authorize(operationFact);
  }

}
