package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.security.assessment.AssessmentCreateAuthorizer;

/**
 * @author CWDS TPT-2 Team
 *     <p>This check must work through the
 *     gov.ca.cwds.cans.security.assessment.AssessmentCreateAuthorizer
 */
@Deprecated
public class ClientCreateAssessmentAuthorizer extends BaseClientAssessmentAuthorizer {

  @Inject
  public ClientCreateAssessmentAuthorizer(AssessmentCreateAuthorizer authorizer) {
    super(authorizer);
  }
}
