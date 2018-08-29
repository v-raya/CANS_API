package gov.ca.cwds.cans.test;

import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_ACCOUNT_FIXTURE;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.eclipse.jetty.util.ssl.SslContextFactory.TRUST_ALL_CERTS;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.io.IOException;
import java.security.SecureRandom;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author denys.davydov
 */
public abstract class AbstractRestClientTestRule implements TestRule {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractRestClientTestRule.class);

  private static final String QUERY_PARAM_TOKEN = "token";
  private final static String DEFAULT_IDENTITY_JSON = fixture(AUTHORIZED_ACCOUNT_FIXTURE);

  ObjectMapper mapper;
  String apiUrl;
  String token;
  protected Client client;

  abstract String generateToken(String identity, String password) throws IOException;

  public AbstractRestClientTestRule withSecurityToken(String identityJsonFilePath) throws IOException {
    final String identity = identityJsonFilePath != null
        ? fixture(identityJsonFilePath)
        : DEFAULT_IDENTITY_JSON;
    this.token = generateToken(identity, null);
    return this;
  }

 String initToken() {
    try {
      return generateToken(DEFAULT_IDENTITY_JSON, null);
    } catch (Exception e) {
      LOG.warn("Cannot generate token");
      return null;
    }
  }

  public WebTarget target(String pathInfo) {
    String restUrl = apiUrl + pathInfo;
    return client.target(restUrl)
        .queryParam(QUERY_PARAM_TOKEN, token)
        .register(new LoggingFilter());
  }

  public ObjectMapper getMapper() {
    return mapper;
  }

  @Override
  public Statement apply(Statement statement, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        JerseyClientBuilder clientBuilder = new JerseyClientBuilder()
            .property(ClientProperties.CONNECT_TIMEOUT, 5000)
            .property(ClientProperties.READ_TIMEOUT, 20000)
            .hostnameVerifier((hostName, sslSession) -> {
              // Just ignore host verification for test purposes
              return true;
            });

        client = clientBuilder.build();
        client.register(new JacksonJsonProvider(mapper));
        client.getSslContext().init(null, TRUST_ALL_CERTS, new SecureRandom());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        statement.evaluate();
      }
    };
  }
}
