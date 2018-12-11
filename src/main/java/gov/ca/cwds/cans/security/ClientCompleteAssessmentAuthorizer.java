package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.security.assessment.AssessmentCompleteAuthorizer;

/**
 * @author CWDS TPT-2 Team
 *     <p>This check must work through the
 *     gov.ca.cwds.cans.security.assessment.AssessmentCompleteAuthorizer
 */
@Deprecated
public class ClientCompleteAssessmentAuthorizer extends BaseClientAssessmentAuthorizer {

  @Inject
  public ClientCompleteAssessmentAuthorizer(AssessmentCompleteAuthorizer authorizer) {
    super(authorizer);
  }
}
