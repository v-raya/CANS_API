package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.test.util.FixtureReader;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/** @author denys.davydov */
public class PersonResourceTest extends AbstractCrudIntegrationTest<PersonDto> {

  private static final String FIXTURES_POST = "fixtures/person-post.json";
  private static final String FIXTURES_PUT = "fixtures/person-put.json";
  private static final String FIXTURES_GET_ALL = "fixtures/person-get-all.json";
  private static final String FIXTURES_SEARCH_CLIENTS_REQUEST =
      "fixtures/person-search-clients-request.json";
  private static final String FIXTURES_SEARCH_CLIENTS_RESPONSE =
      "fixtures/person-search-clients-response.json";

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
