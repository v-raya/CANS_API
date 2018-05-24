package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.test.util.FixtureReader;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/** @author denys.davydov */
public class PersonResourceTest extends AbstractCrudFunctionalTest<PersonDto> {

  private static final String FIXTURES_EMPTY_OBJECT = "fixtures/empty-object.json";
  private static final String FIXTURES_POST = "fixtures/person-post.json";
  private static final String FIXTURES_PUT = "fixtures/person-put.json";
  private static final String FIXTURES_GET_ALL = "fixtures/person-get-all.json";
  private static final String FIXTURES_SEARCH_CLIENTS_REQUEST =
      "fixtures/person-search-clients-request.json";
  private static final String FIXTURES_SEARCH_CLIENTS_RESPONSE =
      "fixtures/person-search-clients-response.json";
  private static final String SIXTY_SYMBOLS_STRING =
      "123456789012345678901234567890123456789012345678901234567890";
  private static final String SIZE_VALIDATION_MESSAGE = "size must be between 1 and 50";

  @Override
  String getPostFixturePath() {
    return FIXTURES_POST;
  }

  @Override
  String getPutFixturePath() {
    return FIXTURES_PUT;
  }

  @Override
  String getApiPath() {
    return PEOPLE;
  }

  @Test
  public void person_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }

  @Test
  public void personPost_fails_whenNullOrEmptyValidationFails() throws IOException {
    // given
    final Entity input = FixtureReader.readRestObject(FIXTURES_EMPTY_OBJECT, PersonDto.class);

    // when
    final BaseExceptionResponse actualResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(input)
            .readEntity(BaseExceptionResponse.class);

    final Set<String> actualViolatedFields =
        actualResponse
            .getIssueDetails()
            .stream()
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());

    // then
    assertThat(actualViolatedFields.size(), is(5));
    assertThat(
        actualViolatedFields,
        containsInAnyOrder("firstName", "lastName", "caseId", "county", "personRole"));
  }

  @Test
  public void personPost_fails_whenFieldsLengthValidationFails() throws IOException {
    // given
    final PersonDto input = new PersonDto();
    input.setFirstName(SIXTY_SYMBOLS_STRING);
    input.setLastName(SIXTY_SYMBOLS_STRING);
    input.setCaseId(SIXTY_SYMBOLS_STRING);

    // when
    final BaseExceptionResponse actualResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(BaseExceptionResponse.class);

    final Set<String> actualViolatedFields =
        actualResponse
            .getIssueDetails()
            .stream()
            .filter(issue -> SIZE_VALIDATION_MESSAGE.equals(issue.getUserMessage()))
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());

    // then
    assertThat(actualViolatedFields.size(), is(3));
    assertThat(actualViolatedFields, containsInAnyOrder("firstName", "lastName", "caseId"));
  }

  @Test
  public void getAllPeople_success_whenFound() throws IOException {
    // given
    final PersonDto[] expected = FixtureReader.readObject(FIXTURES_GET_ALL, PersonDto[].class);

    // when
    final PersonDto[] actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(PersonDto[].class);

    // then
    assertThat(actual, is(expected));
  }

  @Test
  public void searchPeople_success_whenSearchingForClients() throws IOException {
    // given
    final PersonDto[] expected =
        FixtureReader.readObject(FIXTURES_SEARCH_CLIENTS_RESPONSE, PersonDto[].class);
    final Entity searchInput =
        FixtureReader.readRestObject(FIXTURES_SEARCH_CLIENTS_REQUEST, SearchPersonRequest.class);

    // when
    final PersonDto[] actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(searchInput)
            .readEntity(PersonDto[].class);

    // then
    assertThat(actual, is(expected));
  }
}
