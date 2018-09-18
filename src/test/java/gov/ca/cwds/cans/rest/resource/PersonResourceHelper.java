package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_ACCOUNT_FIXTURE;
import static gov.ca.cwds.cans.test.util.TestUtils.SLASH;

import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import java.io.IOException;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PersonResourceHelper {

  private AbstractRestClientTestRule clientTestRule;

  private final Stack<Long> cleanUpPeopleIds = new Stack<>();

  public PersonResourceHelper(AbstractRestClientTestRule clientTestRule) {
    this.clientTestRule = clientTestRule;
  }
  
  public void cleanUp() throws IOException {
    while (!cleanUpPeopleIds.empty()) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(PEOPLE + SLASH + cleanUpPeopleIds.pop())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  public PersonDto postPerson(PersonDto input) throws IOException {
    return postPerson(input, AUTHORIZED_ACCOUNT_FIXTURE);
  }

  public PersonDto postPerson(PersonDto input, String accountFixture) throws IOException {
    PersonDto person = postPersonAndGetResponse(input, accountFixture).readEntity(PersonDto.class);
    cleanUpPeopleIds.push(person.getId());
    return person;
  }

  public Response postPersonAndGetResponse(PersonDto input, String accountFixture) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
  }

  public Response putPerson(String accountFixture, PersonDto input) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE + SLASH + input.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
  }

  public Response getPerson(String accountFixture, Long id) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();
  }

}
