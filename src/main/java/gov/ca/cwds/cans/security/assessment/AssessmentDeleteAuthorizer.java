package gov.ca.cwds.cans.security.assessment;

public class AssessmentDeleteAuthorizer extends AssessmentOperationAuthorizer {

  AssessmentDeleteAuthorizer() {
    super(AssessmentOperation.delete);
  }
}
