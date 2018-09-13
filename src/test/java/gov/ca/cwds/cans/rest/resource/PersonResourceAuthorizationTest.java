package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.test.util.FixtureReader;
import java.io.IOException;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

public class PersonResourceAuthorizationTest extends AbstractFunctionalTest {

  private static final String FIXTURES_POST_SENSITIVE = "fixtures/person-post-sensitive.json";

  private static final CountyDto EL_DORADO_COUNTY = new CountyDto();
  static {
    EL_DORADO_COUNTY.setId(9L);
    EL_DORADO_COUNTY.setName("El Dorado");
    EL_DORADO_COUNTY.setExportId("09");
    EL_DORADO_COUNTY.setExternalId("1076");
  }

  private final Stack<Long> cleanUpPeopleIds = new Stack<>();

  @After
  public void tearDown() throws IOException {
    while (!cleanUpPeopleIds.empty()) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(PEOPLE + SLASH + cleanUpPeopleIds.pop())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Test
  public void personGet_Success_whenHasSensitivePrivilege() throws IOException {
    final PersonDto person = postPerson(
        FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    Response response = getPerson(AUTHORIZED_ACCOUNT_FIXTURE, person.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_OK));
  }

  @Test
  public void personGet_Unauthorized_whenNoSensitivePrivilege() throws IOException {
    final PersonDto person = postPerson(
        FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    Response response = getPerson(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE, person.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personGet_Unauthorized_whenCountyIsNotTheSame() throws IOException {
    PersonDto person = FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class);
    person.setCounty(EL_DORADO_COUNTY);
    final PersonDto postedPerson = postPerson(person);
    Response response = getPerson(AUTHORIZED_ACCOUNT_FIXTURE, postedPerson.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPut_Success_whenHasSensitivePrivilege() throws IOException {
    final PersonDto person = postPerson(
        FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    person.setMiddleName("");
    Response response = putPerson(AUTHORIZED_ACCOUNT_FIXTURE, person);
    assertThat(response.getStatus(), is(HttpStatus.SC_OK));
  }

  @Test
  public void personPut_Unauthorized_whenNoSensitivePrivilege() throws IOException {
    final PersonDto person = postPerson(
        FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    person.setMiddleName("");
    Response response = putPerson(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE, person);
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPut_Unauthorized_whenCountyIsNotTheSame() throws IOException {
    PersonDto person = FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class);
    person.setCounty(EL_DORADO_COUNTY);
    final PersonDto postedPerson = postPerson(person);
    postedPerson.setMiddleName("");
    Response response = putPerson(AUTHORIZED_ELDORADO_ACCOUNT_FIXTURE, person);
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  private PersonDto postPerson(PersonDto input) throws IOException {
    PersonDto person = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(PersonDto.class);
    cleanUpPeopleIds.push(person.getId());
    return person;
  }

  private Response putPerson(String accountFixture, PersonDto input) throws IOException {
    Response response = clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE + SLASH + input.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
    return response;
  }

  private Response getPerson(String accountFixture, Long id) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();
  }
}
