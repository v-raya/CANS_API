package gov.ca.cwds.cans.security;

public class AssessmentWriteAuthorizer extends BaseAssessmentAuthorizer {

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
