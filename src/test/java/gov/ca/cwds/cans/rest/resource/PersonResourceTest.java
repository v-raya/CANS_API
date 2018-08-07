package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.test.util.FixtureReader;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Assume;
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
  private static final String LONG_ALPHA_SYMBOLS_STRING =
      "abcdefghijklmnopqrstuvxyza";
  private static final String SIZE_VALIDATION_MESSAGE_START = "size must be between";
  private final Set<Long> cleanUpPeopleIds = new HashSet<>();

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

  @After
  public void tearDown() throws IOException {
    for (Long personId : cleanUpPeopleIds) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(PEOPLE + SLASH + personId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
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
        containsInAnyOrder("firstName", "lastName", "externalId", "county", "personRole"));
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
    assertThat(actualViolatedFields, containsInAnyOrder( "firstName", "middleName", "lastName", "suffix"));
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
    input.getCases().add(new CaseDto().setExternalId("1234"));

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
    assertThat(actualViolatedFields.size(), is(2));
    assertThat(actualViolatedFields, containsInAnyOrder( "caseId", "externalId"));
  }

  @Test
  public void getAllPeople_success_whenFoundInMemoryOnly() throws IOException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

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
    final List<PersonDto> actualList = Arrays.asList(actual);
    for (PersonDto person : expected) {
      assertThat(actualList, hasItem(person));
    }
  }

  @Test
  public void searchPeople_success_whenSearchingForClientsInMemoryOnly() throws IOException {
    Assume.assumeTrue(FunctionalTestContextHolder.isInMemoryTestRunning);

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
    final List<PersonDto> actualList = Arrays.asList(actual);
    for (PersonDto person : expected) {
      assertThat(actualList, hasItem(person));
    }
  }

  @Test
  public void searchPeople_success_whenSearchingForClients() throws IOException {
    // given
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
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void postPerson_success_whenPersonHasNoCases() throws IOException {
    // given
    final PersonDto inputPerson = FixtureReader.readObject(FIXTURES_POST, PersonDto.class);
    inputPerson.getCases().clear();

    // when
    final PersonDto actualPerson =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputPerson, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    cleanUpPeopleIds.add(actualPerson.getId());

    // then
    actualPerson.setId(null);
    assertThat(actualPerson, is(inputPerson));
  }

  @Test
  public void postPerson_success_whenPersonHasExistentAndNewCases() throws IOException {
    // given
    final PersonDto person = FixtureReader.readObject(FIXTURES_POST, PersonDto.class);
    final PersonDto postedPerson =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    cleanUpPeopleIds.add(postedPerson.getId());
    person.getCases().add(new CaseDto().setExternalId("2000-123-1234-12345678"));

    // when
    final PersonDto actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    cleanUpPeopleIds.add(actual.getId());

    // then
    final Set<CaseDto> caseIds =
        actual
            .getCases()
            .stream()
            .filter(aCase -> aCase.getId() != null)
            .collect(Collectors.toSet());
    assertThat(caseIds.size(), is(2));
    final List<String> externalIds =
        actual.getCases().stream().map(CaseDto::getExternalId).collect(Collectors.toList());
    assertThat(externalIds.size(), is(2));
    assertThat(externalIds, containsInAnyOrder("4321-321-4321-87654321", "2000-123-1234-12345678"));
  }

  @Test
  public void putPerson_success_whenUpdatingCasesListWithExistingAndNewCases() throws IOException {
    // given
    final PersonDto person = FixtureReader.readObject(FIXTURES_POST, PersonDto.class);
    final List<CaseDto> cases = person.getCases();
    cases.add(new CaseDto().setExternalId("2000-123-1234-12345678"));
    cases.add(new CaseDto().setExternalId("3000-123-1234-12345678"));
    final PersonDto postedPerson =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    cleanUpPeopleIds.add(postedPerson.getId());
    final List<CaseDto> createdPersonCases = postedPerson.getCases();
    createdPersonCases.remove(0);
    createdPersonCases.get(0).setExternalId("2222-123-1234-12345678");
    createdPersonCases.add(new CaseDto().setExternalId("4000-123-1234-12345678"));

    // when
    final PersonDto actual =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(PEOPLE + SLASH + postedPerson.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedPerson, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(PersonDto.class);
    cleanUpPeopleIds.add(actual.getId());

    // then
    final Set<CaseDto> caseIds =
        actual
            .getCases()
            .stream()
            .filter(aCase -> aCase.getId() != null)
            .collect(Collectors.toSet());
    assertThat(caseIds.size(), is(3));
    final List<String> externalIds =
        actual.getCases().stream().map(CaseDto::getExternalId).collect(Collectors.toList());
    assertThat(externalIds.size(), is(3));
    assertThat(
        externalIds,
        containsInAnyOrder(
            "2222-123-1234-12345678", "3000-123-1234-12345678", "4000-123-1234-12345678"));
  }
}
