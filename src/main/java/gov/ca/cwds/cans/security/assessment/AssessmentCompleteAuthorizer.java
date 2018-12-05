package gov.ca.cwds.cans.security.assessment;

public class AssessmentCompleteAuthorizer extends AssessmentOperationAuthorizer {

  AssessmentCompleteAuthorizer() {
    super(AssessmentOperation.complete);
  }
}
