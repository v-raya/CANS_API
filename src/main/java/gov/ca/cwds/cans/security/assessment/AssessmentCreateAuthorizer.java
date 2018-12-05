package gov.ca.cwds.cans.security.assessment;

public class AssessmentCreateAuthorizer extends AssessmentOperationAuthorizer {

  AssessmentCreateAuthorizer() {
    super(AssessmentOperation.create);
  }
}
