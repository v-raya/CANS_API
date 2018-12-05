package gov.ca.cwds.cans.security.assessment;

public class AssessmentUpdateAuthorizer extends AssessmentOperationAuthorizer {

  AssessmentUpdateAuthorizer() {
    super(AssessmentOperation.update);
  }
}
