package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    final PersonDto person = postSensitivePerson();
    Response response = getPersonForAccountById(AUTHORIZED_ACCOUNT_FIXTURE, person.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_OK));
  }

  @Test
  public void personGet_Unauthorized_whenNoSensitivePrivilege() throws IOException {
    final PersonDto person = postSensitivePerson();
    Response response = getPersonForAccountById(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE,
        person.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_UNAUTHORIZED));
  }

  private PersonDto postSensitivePerson() throws IOException {
    final Entity input = FixtureReader.readRestObject(FIXTURES_POST_SENSITIVE, PersonDto.class);
    PersonDto person = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(input)
        .readEntity(PersonDto.class);
    cleanUpPeopleIds.push(person.getId());
    return person;
  }


  private Response getPersonForAccountById(String accountFixture, Long id) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();
  }
}
