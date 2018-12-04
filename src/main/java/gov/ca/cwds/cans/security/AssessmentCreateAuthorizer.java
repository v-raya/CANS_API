package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;

/** @author CWDS TPT-2 Team */
public class AssessmentCreateAuthorizer extends BaseAuthorizer<Client, String> {

  @Inject private ClientCheckHelper clientCheckHelper;

  @Override
  protected boolean checkId(String clientId) {
    return clientCheckHelper.checkWriteAssessmentByClientId(clientId);
  }
}
