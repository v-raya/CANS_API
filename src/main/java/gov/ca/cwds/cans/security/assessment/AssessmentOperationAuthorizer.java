package gov.ca.cwds.cans.security.assessment;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.security.assessment.facts.AssessmentOperationFact;
import gov.ca.cwds.cans.service.CansRulesService;
import gov.ca.cwds.cans.service.PersonService;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AssessmentOperationAuthorizer extends AssessmentAccessAuthorizer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentOperationAuthorizer.class);
  private final AssessmentOperation operation;
  @Inject private CansRulesService rulesService;
  @Inject private PersonService clientsService;

  AssessmentOperationAuthorizer(AssessmentOperation operation) {
    this.operation = operation;
  }

  protected boolean checkInstance(Assessment assessment) {
    Person person = clientsService.findByExternalId(assessment.getPerson().getExternalId());
    assessment.setPerson(person);
    boolean isAssessmentAccessible = super.checkInstance(assessment);
    return checkOperation(assessment, isAssessmentAccessible);
  }

  boolean checkOperation(Assessment assessment, boolean isAssessmentAccessible) {
    AssessmentOperationFact operationFact =
        new AssessmentOperationFact(
            operation, assessment, PrincipalUtils.getPrincipal(), isAssessmentAccessible);
    boolean result = rulesService.authorize(operationFact);
    if (!result) {
      LOG.info(
          "Authorization: operation [{}] for assessment with status [{}] and county [{}]"
              + " is not allowed for user [{}] from county [{}]",
          operationFact.getOperation(),
          assessment.getStatus(),
          assessment.getPerson().getCounty(),
          operationFact.getUser().getStaffId(),
          operationFact.getUser().getCountyCwsCode());
    }
    return result;
  }
}
