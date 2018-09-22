package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.CHECK_PERMISSION;
import static gov.ca.cwds.cans.Constants.API.COUNTIES;
import static gov.ca.cwds.cans.Constants.API.I18N;
import static gov.ca.cwds.cans.Constants.API.INSTRUMENTS;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.Constants.API.SECURITY;
import static gov.ca.cwds.cans.Constants.API.START;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Test;

public class AuthorizationResourceTest extends AbstractFunctionalTest {

  private static final String START_ASSESSMENT_REQUEST = StartAssessmentRequest.class.getCanonicalName();
  private static final String INSTRUMENT_DTO = InstrumentDto.class.getCanonicalName();
  private static final String PERSON_DTO = PersonDto.class.getCanonicalName();

  private static final String ID = "1";
  private static final String KEY_PREFIX = "instrument.1.";
  private static final String LANGUAGE = "en";
  private static final String FIXTURES_INSTRUMENT_PUT_JSON = "fixtures/instrument-put.json";
  private static final String FIXTURES_INSTRUMENT_POST_JSON = "fixtures/instrument-post.json";
  private static final String FIXTURES_PERSON_PUT_JSON = "fixtures/person-put.json";
  private static final String FIXTURES_PERSON_POST_JSON = "fixtures/person-post.json";

  private enum HttpMethod {
    POST,
    PUT,
    GET,
    DELETE
  }

  @Test
  public void assessmentEndpoints_failed_whenUnauthorizedUser()
      throws IOException, ClassNotFoundException {
    assertAssessmentEndpointsAreSecured(NOT_AUTHORIZED_ACCOUNT_FIXTURE);
  }

  @Test
  public void countiesEndpoint_failed_whenUnauthorizedUser()
      throws IOException, ClassNotFoundException {
    assertEndpointIsSecured(COUNTIES, null,
        null, HttpMethod.GET, NOT_AUTHORIZED_ACCOUNT_FIXTURE);
  }

  @Test
  public void i18nEndpoints_failed_whenUnauthorizedUser()
      throws IOException, ClassNotFoundException {
    assertI18nEndpointsAreSecured(NOT_AUTHORIZED_ACCOUNT_FIXTURE);
  }

  @Test
  public void instrumentEndpoints_failed_whenUnauthorizedUser()
      throws IOException, ClassNotFoundException {
    assertInstrumentEndpointsAreSecured(NOT_AUTHORIZED_ACCOUNT_FIXTURE);
  }

  @Test
  public void peopleEndpoints_failed_whenUnauthorizedUser()
      throws IOException, ClassNotFoundException {
    assertPeopleEndpointsAreSecured(NOT_AUTHORIZED_ACCOUNT_FIXTURE);
  }

  @Test
  public void securityEndpoints_failed_whenUnauthorizedUser()
      throws IOException, ClassNotFoundException {
    assertEndpointIsSecured(SECURITY + SLASH + CHECK_PERMISSION + SLASH + "assessment:write:1", null,
        null, HttpMethod.GET, NOT_AUTHORIZED_ACCOUNT_FIXTURE);
  }

  private void assertI18nEndpointsAreSecured(String securityTokenFixturePath)
      throws IOException, ClassNotFoundException {
    assertEndpointIsSecured(I18N + SLASH + KEY_PREFIX, null,
        null, HttpMethod.GET, securityTokenFixturePath);

    assertEndpointIsSecured(I18N + SLASH + KEY_PREFIX + SLASH + LANGUAGE, null,
        null, HttpMethod.GET, securityTokenFixturePath);
  }

  private void assertInstrumentEndpointsAreSecured(String securityTokenFixturePath)
      throws IOException, ClassNotFoundException {
    assertEndpointIsSecured(INSTRUMENTS + SLASH + ID, null,
        null, HttpMethod.GET, securityTokenFixturePath);

    assertEndpointIsSecured(INSTRUMENTS + SLASH + ID + I18N + LANGUAGE, null,
        null, HttpMethod.GET, securityTokenFixturePath);

    assertEndpointIsSecured(INSTRUMENTS + SLASH + ID, FIXTURES_INSTRUMENT_PUT_JSON,
        INSTRUMENT_DTO, HttpMethod.PUT, securityTokenFixturePath);

    assertEndpointIsSecured(INSTRUMENTS, FIXTURES_INSTRUMENT_POST_JSON,
        INSTRUMENT_DTO, HttpMethod.POST, securityTokenFixturePath);

    assertEndpointIsSecured(INSTRUMENTS + SLASH + ID, null,
        null, HttpMethod.DELETE, securityTokenFixturePath);
  }

  private void assertPeopleEndpointsAreSecured(String securityTokenFixturePath)
      throws IOException, ClassNotFoundException {
    assertEndpointIsSecured(PEOPLE + SLASH + ID, null,
        null, HttpMethod.GET, securityTokenFixturePath);

    assertEndpointIsSecured(PEOPLE + SLASH + ID, FIXTURES_PERSON_PUT_JSON,
        PERSON_DTO, HttpMethod.PUT, securityTokenFixturePath);

    assertEndpointIsSecured(PEOPLE, FIXTURES_PERSON_POST_JSON,
        PERSON_DTO, HttpMethod.POST, securityTokenFixturePath);

    assertEndpointIsSecured(PEOPLE + SLASH + SEARCH, FIXTURES_PERSON_POST_JSON,
        PERSON_DTO, HttpMethod.POST, securityTokenFixturePath);

    assertEndpointIsSecured(PEOPLE + SLASH + ID, null,
        null, HttpMethod.DELETE, securityTokenFixturePath);
  }

  private void assertAssessmentEndpointsAreSecured(String securityTokenFixturePath)
      throws IOException, ClassNotFoundException {
    assertEndpointIsSecured(ASSESSMENTS + SLASH + START, FIXTURE_START,
        START_ASSESSMENT_REQUEST, HttpMethod.POST, securityTokenFixturePath);

    assertEndpointIsSecured(ASSESSMENTS + SLASH + ID, null,
        null, HttpMethod.GET, securityTokenFixturePath);

    assertEndpointIsSecured(ASSESSMENTS + SLASH + ID, null,
        null, HttpMethod.DELETE, securityTokenFixturePath);

    assertEndpointIsSecured(ASSESSMENTS + SLASH + ID, FIXTURE_START,
        START_ASSESSMENT_REQUEST, HttpMethod.PUT, securityTokenFixturePath);

    assertEndpointIsSecured(ASSESSMENTS, FIXTURE_START,
        START_ASSESSMENT_REQUEST, HttpMethod.POST, securityTokenFixturePath);
  }

  private void assertEndpointIsSecured(String resourceUrl, String requestFixture, String requestClass,
      HttpMethod httpMethod, String securityTokenFixturePath)
      throws ClassNotFoundException, IOException {
    // given
    Object request = null;
    if (requestFixture != null) {
      request = readObject(requestFixture, Class.forName(requestClass));
    }

    // when
    Response response = null;
    switch (httpMethod) {
      case GET:
        response =
            clientTestRule
                .withSecurityToken(securityTokenFixturePath)
                .target(resourceUrl)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        break;
      case POST:
        response =
            clientTestRule
                .withSecurityToken(securityTokenFixturePath)
                .target(resourceUrl)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
        break;
      case PUT:
        response =
            clientTestRule
                .withSecurityToken(securityTokenFixturePath)
                .target(resourceUrl)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
        break;
      case DELETE:
        response =
            clientTestRule
                .withSecurityToken(securityTokenFixturePath)
                .target(resourceUrl)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        break;
    }

    // then
    assertThat(response.getStatus(), is(403));
  }

}
