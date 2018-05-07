package gov.ca.cwds.cans.test;

import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.test.support.JWTTokenProvider;
import gov.ca.cwds.test.support.JsonIdentityAuthParams;
import io.dropwizard.testing.junit.DropwizardAppRule;

/**
 * @author denys.davydov
 */
public class InMemoryIntegrationRestClientTestRule extends AbstractRestClientTestRule {

  private JWTTokenProvider tokenProvider = new JWTTokenProvider();

  public InMemoryIntegrationRestClientTestRule(DropwizardAppRule<CansConfiguration> dropWizardApplication) {
    token = initToken();
    mapper = dropWizardApplication.getObjectMapper();
    apiUrl = String.format("http://localhost:%s/", dropWizardApplication.getLocalPort());
  }

  @Override
  String generateToken(String identity, String password) {
    final JsonIdentityAuthParams authParams = new JsonIdentityAuthParams(identity);
    return tokenProvider.doGetToken(authParams);
  }

}
