package gov.ca.cwds.cans.test;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.cans.test.util.TestUtils;
import gov.ca.cwds.cans.util.Require;
import io.dropwizard.jackson.Jackson;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.glassfish.jersey.client.ClientProperties;

/**
 * @author denys.davydov
 */
public class IntegrationRestClientTestRule extends AbstractRestClientTestRule {

  private static final String PATH_PERRY_LOGIN = "perry/login";
  private static final String PATH_PERRY_AUTHN_LOGIN = "perry/authn/login";
  private static final String PATH_PERRY_AUTHN_TOKEN = "perry/authn/token";
  private static final String CALLBACK = "callback";
  private static final String ACCESS_CODE = "accessCode";
  private static final String FORM_PARAM_USERNAME = "username";
  private static final String FORM_PARAM_PASSWORD = "password";

  private final WebTarget webTarget;

  public IntegrationRestClientTestRule() throws KeyManagementException {
    mapper = Jackson.newObjectMapper();
    ObjectMapperUtils.configureObjectMapper(mapper);
    apiUrl = TestUtils.getApiUrl();
    Require.requireNotNullAndNotEmpty(apiUrl);

    final String perryUrl = TestUtils.getPerryUrl();
    Require.requireNotNullAndNotEmpty(perryUrl);

    final Client client = ClientBuilder.newClient();
    client.getSslContext().init(null, TRUST_ALL_CERTS, new SecureRandom());
    webTarget = client.target(perryUrl);
    token = initToken();
  }

  @Override
  String generateToken(String identity, String password) {
    final Map<String, NewCookie> cookies = postSecurityFormAndGetJSessionIdCookie(identity, password);
    final String accessCode = getAccessCodeFromPerry(cookies);
    return getTokenFromPerry(accessCode);
  }

  private Map<String, NewCookie> postSecurityFormAndGetJSessionIdCookie(String identity, String password) {
    final Entity<Form> entity = prepareFormEntity(identity, password);
    final Response response = webTarget.path(PATH_PERRY_LOGIN)
        .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
        .request()
        .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
        .post(entity);
    return response.getCookies();
  }

  private Entity<Form> prepareFormEntity(String identity, String password) {
    final Form form = new Form();
    form.param(FORM_PARAM_USERNAME, identity.replaceAll("\n", StringUtils.EMPTY));
    if (StringUtils.isNotBlank(password)) {
      form.param(FORM_PARAM_PASSWORD, password);
    }
    return Entity.form(form);
  }

  private String getAccessCodeFromPerry(Map<String, NewCookie> cookies) {
    final Builder request = webTarget
        .path(PATH_PERRY_AUTHN_LOGIN)
        .queryParam(CALLBACK, apiUrl)
        .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
        .request();
    cookies.entrySet().forEach(
        entry -> request.cookie(entry.getKey(), entry.getValue().getValue())
    );
    final Response response = request.get();

    return parseAccessCode(response.getLocation());
  }

  private String parseAccessCode(URI uri) {
    final List<NameValuePair> params = URLEncodedUtils.parse(uri,  "UTF-8");
    final Optional<String> accessCodeOpt = params.stream()
        .filter(nameValuePair -> nameValuePair.getName().equals(ACCESS_CODE))
        .findFirst()
        .map(NameValuePair::getValue);
    return accessCodeOpt.get();
  }

  private String getTokenFromPerry(String accessCode) {
    final Response response = webTarget
        .path(PATH_PERRY_AUTHN_TOKEN)
        .queryParam(ACCESS_CODE, accessCode)
        .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
        .request()
        .get();

    return response.readEntity(String.class);
  }

}
