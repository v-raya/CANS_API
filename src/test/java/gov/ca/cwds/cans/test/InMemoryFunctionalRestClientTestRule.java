package gov.ca.cwds.cans.test;

import gov.ca.cwds.cans.CansConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import javax.ws.rs.client.WebTarget;

/**
 * @author denys.davydov
 */
public class InMemoryFunctionalRestClientTestRule extends AbstractRestClientTestRule {

  public InMemoryFunctionalRestClientTestRule(
      DropwizardAppRule<CansConfiguration> dropWizardApplication) {
    token = initToken();
    mapper = dropWizardApplication.getObjectMapper();
    apiUrl = String.format("http://localhost:%s/", dropWizardApplication.getLocalPort());
  }

  @Override
  String generateToken(String identity, String password) {
    throw new UnsupportedOperationException(
        "InMemoryFunctionalRestClientTestRule.generateToken(String identity, String password)");
  }

  public AbstractRestClientTestRule withSecurityToken(String identityJsonFilePath) {
    this.token = identityJsonFilePath;
    return this;
  }

  public WebTarget target(String pathInfo) {
    String restUrl = apiUrl + pathInfo;
    return client.target(restUrl)
        .queryParam("pathToPrincipalFixture", token)
        .register(new LoggingFilter());
  }

}
