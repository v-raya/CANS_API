package gov.ca.cwds.cans.test;

import static org.eclipse.jetty.util.ssl.SslContextFactory.TRUST_ALL_CERTS;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.cans.test.util.TestUtils;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.test.support.JsonIdentityAuthParams;
import gov.ca.cwds.test.support.PerryV2DevModeTokenProvider;
import io.dropwizard.jackson.Jackson;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/** @author denys.davydov */
public class IntegrationRestClientTestRule extends AbstractRestClientTestRule {

  private final PerryV2DevModeTokenProvider tokenProvider;

  public IntegrationRestClientTestRule() throws KeyManagementException {
    mapper = Jackson.newObjectMapper();
    ObjectMapperUtils.configureObjectMapper(mapper);

    apiUrl = TestUtils.getApiUrl();
    Require.requireNotNullAndNotEmpty(apiUrl);

    final String perryUrl = TestUtils.getPerryUrl();
    Require.requireNotNullAndNotEmpty(perryUrl);

    final String perryLoginFormUrl = TestUtils.getPerryLoginFormUrl();
    Require.requireNotNullAndNotEmpty(perryLoginFormUrl);

    final Client client = ClientBuilder.newClient();
    client.getSslContext().init(null, TRUST_ALL_CERTS, new SecureRandom());
    tokenProvider = new PerryV2DevModeTokenProvider(client, perryUrl, perryLoginFormUrl);
    token = initToken();
  }

  @Override
  String generateToken(String identity, String password) {
    final JsonIdentityAuthParams authParams = new JsonIdentityAuthParams(identity);
    return tokenProvider.doGetToken(authParams);
  }
}
