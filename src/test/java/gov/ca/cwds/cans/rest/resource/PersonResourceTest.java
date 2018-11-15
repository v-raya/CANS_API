package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.test.util.AssertFixtureUtils.assertResponseByFixturePath;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.PaginationDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.PersonShortDto;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonResponse;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.test.util.FixtureReader;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/** @author denys.davydov */
public class PersonResourceTest extends AbstractCrudFunctionalTest<PersonDto> {

  private static final String FIXTURES_EMPTY_OBJECT = "fixtures/empty-object.json";
  private static final String FIXTURES_POST = "fixtures/person-post.json";
  private static final String FIXTURES_POST_WITH_SEALED_SENSITIVITY_TYPE =
      "fixtures/person-post-with-sensitivity-type.json";
  private static final String FIXTURES_PUT = "fixtures/person-put.json";
  private static final String FIXTURES_PERSON_SINGLE_COUNTY = "fixtures/person-single-county.json";
  private static final String FIXTURES_SEARCH_CLIENTS_REQUEST =
      "fixtures/person-search-clients-request.json";
  private static final String FIXTURES_SEARCH_CLIENTS_RESPONSE =
      "fixtures/person-search-clients-response.json";
  private static final String LONG_ALPHA_SYMBOLS_STRING = "abcdefghijklmnopqrstuvxyza";
  private static final String SIZE_VALIDATION_MESSAGE_START = "size must be between";
  private static final String EXTERNAL_ID = "6666-6666-6666-6666666";

  private PersonResourceHelper personHelper;

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

  @Before
  public void before() {
    personHelper = new PersonResourceHelper(clientTestRule);
  }

  @After
  public void tearDown() throws IOException {
    personHelper.cleanUp();
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
        containsInAnyOrder("firstName", "lastName", "externalId", "county", "personRole"));
    // dob error is not present on null dob
    assertThat(actualViolatedFields, not(containsInAnyOrder("dob")));
  }

  @Test
  public void personPost_fails_whenFieldsLengthValidationFails() throws IOException {
    // given
    final PersonDto input = new PersonDto();
    input.setFirstName(LONG_ALPHA_SYMBOLS_STRING);
    input.setMiddleName(LONG_ALPHA_SYMBOLS_STRING);
    input.setLastName(LONG_ALPHA_SYMBOLS_STRING);
    input.setSuffix(LONG_ALPHA_SYMBOLS_STRING);

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
            .filter(issue -> issue.getUserMessage().startsWith(SIZE_VALIDATION_MESSAGE_START))
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());

    // then
    assertThat(actualViolatedFields.size(), is(4));
    assertThat(
        actualViolatedFields, containsInAnyOrder("firstName", "middleName", "lastName", "suffix"));
  }

  @Test
  public void personPost_fails_whenValidationIssues() throws IOException {
    // given
    final PersonDto input = new PersonDto();
    input.setFirstName("123");
    input.setMiddleName("123");
    input.setLastName("123");
    input.setSuffix("123");
    input.setExternalId("123");
    input.setPersonRole(PersonRole.CLIENT);
    input.setCounty(new CountyDto().setExportId("1"));
    input.setDob(LocalDate.now().plusDays(1));

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
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());

    // then
    assertThat(actualViolatedFields.size(), is(1));
    assertThat(actualViolatedFields, containsInAnyOrder("dob"));
    // valid error message is present for dob in future
    assertThat(
        actualResponse
            .getIssueDetails()
            .stream()
            .anyMatch(
                issueDetails ->
                    issueDetails
                        .getUserMessage()
                        .equals("Date of birth must not be a future date")),
        is(true));
  }

  @Test
  public void searchPeople_success_whenSearchingForClients_inMemoryOnly() throws IOException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

    // given
    final PersonShortDto[] expected =
        FixtureReader.readObject(FIXTURES_SEARCH_CLIENTS_RESPONSE, PersonShortDto[].class);
    final Entity searchInput =
        FixtureReader.readRestObject(FIXTURES_SEARCH_CLIENTS_REQUEST, SearchPersonRequest.class);

    // when
    final SearchPersonResponse actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(searchInput)
            .readEntity(SearchPersonResponse.class);

    // then
    for (PersonShortDto person : expected) {
      assertThat(actual.getRecords(), hasItem(person));
    }
  }

  @Test
  public void searchPeople_success_whenSearchingForClientsFiltersCounty_inMemoryOnly()
      throws IOException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

    // given
    final Entity searchInput =
        FixtureReader.readRestObject(FIXTURES_SEARCH_CLIENTS_REQUEST, SearchPersonRequest.class);
    final PersonShortDto singleCountyPerson =
        FixtureReader.readObject(FIXTURES_PERSON_SINGLE_COUNTY, PersonShortDto.class);

    // when
    final Collection<PersonShortDto> actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(searchInput)
            .readEntity(SearchPersonResponse.class)
            .getRecords();

    // then
    assertThat(actual.size(), is(1));
    assertThat(actual.iterator().next(), is(singleCountyPerson));
  }

  @Test
  public void searchPeople_metaDataAsExpected_whenUserHasNoSensitivePrivilege_inMemoryOnly()
      throws IOException, JSONException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

    // given
    final SearchPersonRequest searchPersonRequest =
        new SearchPersonRequest()
            .setPersonRole(PersonRole.CLIENT)
            .setPagination(new PaginationDto().setPage(0).setPageSize(10));

    // when
    final Response actualResponse =
        clientTestRule
            .withSecurityToken(STATE_OF_CA_NO_SENSITIVITY)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(searchPersonRequest, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertResponseByFixturePath(
        actualResponse,
        "fixtures/people-search/people-search-filter-no-sensitivity-for-user-response.json");
  }

  @Test
  public void searchPeople_success_whenFilteringByNames_inMemoryOnly()
      throws IOException, JSONException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

    // given
    final SearchPersonRequest searchPersonRequest =
        new SearchPersonRequest()
            .setPersonRole(PersonRole.CLIENT)
            .setFirstName("eter")
            .setMiddleName("Batkovich")
            .setLastName("arke")
            .setPagination(new PaginationDto().setPage(0).setPageSize(10));

    // when
    final Response actualResponse =
        clientTestRule
            .withSecurityToken(STATE_OF_CA_ALL_AUTHORIZED)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(searchPersonRequest, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertResponseByFixturePath(
        actualResponse, "fixtures/people-search/people-search-filter-by-names-response.json");
  }

  @Test
  public void searchPeople_success_whenFilteringByDob_inMemoryOnly()
      throws IOException, JSONException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

    // given
    final SearchPersonRequest searchPersonRequest =
        new SearchPersonRequest()
            .setPersonRole(PersonRole.CLIENT)
            .setDob(LocalDate.of(2008, 1, 31))
            .setPagination(new PaginationDto().setPage(0).setPageSize(10));

    // when
    final Response actualResponse =
        clientTestRule
            .withSecurityToken(STATE_OF_CA_ALL_AUTHORIZED)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(searchPersonRequest, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertResponseByFixturePath(
        actualResponse, "fixtures/people-search/people-search-filter-by-dob-response.json");
  }

  @Test
  public void searchPeople_success_whenRequestSecondPageOfResults_inMemoryOnly()
      throws IOException, JSONException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

    // given
    final SearchPersonRequest searchPersonRequest =
        new SearchPersonRequest()
            .setPersonRole(PersonRole.CLIENT)
            .setFirstName("e")
            .setPagination(new PaginationDto().setPage(1).setPageSize(1));

    // when
    final Response actualResponse =
        clientTestRule
            .withSecurityToken(STATE_OF_CA_ALL_AUTHORIZED)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(searchPersonRequest, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertResponseByFixturePath(
        actualResponse,
        "fixtures/people-search/people-search-pagination-second-page-response.json");
  }

  @Test
  public void searchPeople_validationFails_whenNoPaginationInInput() throws IOException {
    // given
    final SearchPersonRequest searchPersonRequest = new SearchPersonRequest();

    // when
    final BaseExceptionResponse actualResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(searchPersonRequest, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(BaseExceptionResponse.class);

    final Set<String> actualViolatedFields =
        actualResponse
            .getIssueDetails()
            .stream()
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());

    // then
    assertThat(actualViolatedFields.size(), is(1));
    assertThat(actualViolatedFields, containsInAnyOrder("pagination"));
  }

  @Test
  public void searchPeople_validationFails_whenPaginationObjectIsInvalid() throws IOException {
    // given
    final SearchPersonRequest searchPersonRequest =
        new SearchPersonRequest().setPagination(new PaginationDto().setPage(-10).setPageSize(-100));

    // when
    final BaseExceptionResponse actualResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(searchPersonRequest, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(BaseExceptionResponse.class);

    final Set<String> actualViolatedFields =
        actualResponse
            .getIssueDetails()
            .stream()
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());

    // then
    assertThat(actualViolatedFields.size(), is(2));
    assertThat(actualViolatedFields, containsInAnyOrder("pagination.page", "pagination.pageSize"));
  }

  @Test
  public void searchPeople_success_whenSearchingForClients() throws IOException {
    // given
    final Entity searchInput =
        FixtureReader.readRestObject(FIXTURES_SEARCH_CLIENTS_REQUEST, SearchPersonRequest.class);

    // when
    final SearchPersonResponse actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(searchInput)
            .readEntity(SearchPersonResponse.class);

    // then
    assertThat(actual.getRecords(), is(notNullValue()));
  }

  @Test
  public void postPerson_success_whenPersonHasNoCases() throws IOException {
    // given
    final PersonDto inputPerson = personHelper.readPersonDto(FIXTURES_POST);

    // when
    final PersonDto actualPerson =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputPerson, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    personHelper.pushToCleanUpPerson(actualPerson);

    // then
    actualPerson.setId(null);
    assertThat(actualPerson.getExternalId(), is(inputPerson.getExternalId()));
  }

  @Test
  public void postPerson_success_whenPersonHasSensitivityType() throws IOException {
    // given
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST_WITH_SEALED_SENSITIVITY_TYPE);
    final PersonDto postedPerson =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    personHelper.pushToCleanUpPerson(postedPerson);

    // then
    assertThat(postedPerson.getId(), notNullValue());
    assertThat(postedPerson.getSensitivityType(), is(SensitivityType.SEALED));
  }

  @Test
  public void searchPersons_sealedClientIsAvailable_whenUserHasSealedPrivilege()
      throws IOException {

    // given
    final PersonDto person = FixtureReader.readObject(FIXTURES_POST, PersonDto.class);
    person.setSensitivityType(SensitivityType.SEALED);
    person.setExternalId(EXTERNAL_ID);
    personHelper.pushToCleanUpPerson(postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE));

    // when
    Collection<PersonShortDto> people = searchPeople(EXTERNAL_ID, SEALED_EL_DORADO_ACCOUNT_FIXTURE);

    // then
    assertThat(people.size(), is(1));
  }

  @Test
  public void searchPersons_sealedClientIsNotAvailable_whenUserHasNotSealedPrivilege()
      throws IOException {

    // given
    final PersonDto person = FixtureReader.readObject(FIXTURES_POST, PersonDto.class);
    person.setSensitivityType(SensitivityType.SEALED);
    person.setExternalId(EXTERNAL_ID);
    personHelper.pushToCleanUpPerson(postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE));

    // when
    Collection<PersonShortDto> people =
        searchPeople(EXTERNAL_ID, AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE);

    // then
    assertThat(people.size(), is(0));
  }

  @Test
  public void getPerson_authorized_whenUserHasSealedAndClientIsSealed() throws IOException {
    // given
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST);
    person.setSensitivityType(SensitivityType.SEALED);
    PersonDto posted = postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    personHelper.pushToCleanUpPerson(posted);

    // when
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + posted.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

    // then
    assertThat(response.getStatus(), is(200));
    checkMetadataEditable(response, true);
  }

  /* Authorization going to be changed
  @Test
  public void getPerson_unauthorized_whenUserHasSealedAndClientIsSealedButDifferentCounty()
      throws IOException {
    // given
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST);
    person.setSensitivityType(SensitivityType.SEALED);
    PersonDto posted = postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    personHelper.pushToCleanUpPerson(posted);

    // when
    int status =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + posted.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(status, is(403));
  }


  @Test
  public void getPerson_unauthorized_whenUserHasNotSealedAndClientIsSealed() throws IOException {
    // given
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST);
    person.setSensitivityType(SensitivityType.SEALED);
    PersonDto posted = postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    personHelper.pushToCleanUpPerson(posted);

    // when
    int status =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + posted.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(status, is(403));
  }
  */

  @Test
  public void postPerson_fail_whenPersonExistInDatabase() throws IOException {
    // given
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST, false);
    personHelper.postPerson(person, false);
    final PersonDto personDuplicate = personHelper.readPersonDto(FIXTURES_POST, false);
    // when
    Response response =
        personHelper.postPersonAndGetResponse(personDuplicate, AUTHORIZED_ACCOUNT_FIXTURE);
    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_CONFLICT));
  }

  @Test
  public void putPerson_fail_whenPersonExternalIdExistsInDatabase() throws IOException {
    // given
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST, true);
    person = personHelper.postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE, false);
    PersonDto personDuplicate = personHelper.readPersonDto(FIXTURES_POST, false);
    personDuplicate =
        personHelper.postPerson(personDuplicate, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE, true);
    // when
    personDuplicate.setExternalId(person.getExternalId());
    Response response =
        personHelper.putPerson(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE, personDuplicate);
    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_CONFLICT));
  }

  private Response postPersonAndGetResponse(PersonDto person, String accountFixture)
      throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE));
  }

  private PersonDto postPerson(PersonDto person, String accountFixture) throws IOException {
    return postPersonAndGetResponse(person, accountFixture).readEntity(PersonDto.class);
  }

  private Collection<PersonShortDto> searchPeople(String externalId, String accountFixture)
      throws IOException {
    final Entity<SearchPersonRequest> searchInput =
        FixtureReader.readRestObject(FIXTURES_SEARCH_CLIENTS_REQUEST, SearchPersonRequest.class);
    searchInput.getEntity().setExternalId(externalId);
    final SearchPersonResponse actual =
        clientTestRule
            .withSecurityToken(accountFixture)
            .target(PEOPLE + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(searchInput)
            .readEntity(SearchPersonResponse.class);
    return actual.getRecords();
  }
}
