package gov.ca.cwds.cans.security.assessment;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.security.assessment.facts.AssessmentOperationFact;
import gov.ca.cwds.cans.service.CansRulesService;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AssessmentOperationAuthorizer extends AssessmentAccessAuthorizer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentOperationAuthorizer.class);
  private final AssessmentOperation operation;
  @Inject private CansRulesService rulesService;

  AssessmentOperationAuthorizer(AssessmentOperation operation) {
    this.operation = operation;
  }

  @Override
  protected boolean checkInstance(Assessment assessment) {
    boolean isAssessmentAccessible = super.checkInstance(assessment);
    return checkOperation(assessment, isAssessmentAccessible);
  }

  boolean checkOperation(Assessment assessment, boolean isAssessmentAccessible) {
    AssessmentOperationFact operationFact =
        new AssessmentOperationFact(
            operation, assessment, PrincipalUtils.getPrincipal(), isAssessmentAccessible);
    boolean result = rulesService.authorize(operationFact);
    LOG.info(
        "Authorization: client [{}], operation [{}] for assessment [{}] with status [{}] and county [{}]"
            + " for user [{}] from county [{}] with result [{}]",
        assessment.getPerson().getExternalId(),
        operationFact.getOperation(),
        assessment.getId(),
        assessment.getStatus(),
        assessment.getPerson().getCounty(),
        operationFact.getUser().getStaffId(),
        operationFact.getUser().getCountyCwsCode(),
        result);
    return result;
  }
}
