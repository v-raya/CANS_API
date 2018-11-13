package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_ACCOUNT_FIXTURE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static gov.ca.cwds.cans.test.util.TestUtils.SLASH;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PersonResourceHelper {

  private final Stack<PersonDto> cleanUpPeople = new Stack<>();
  private AbstractRestClientTestRule clientTestRule;
  private Random random = new Random();

  public PersonResourceHelper(AbstractRestClientTestRule clientTestRule) {
    this.clientTestRule = clientTestRule;
  }

  public void cleanUp() throws IOException {
    while (!cleanUpPeople.empty()) {
      PersonDto personToDelete = cleanUpPeople.pop();

      clientTestRule
          .withSecurityToken(findUserAccountForDelete(personToDelete.getCounty()))
          .target(PEOPLE + SLASH + personToDelete.getId())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  public void pushToCleanUpPerson(PersonDto person) {
    cleanUpPeople.push(person);
  }

  public PersonDto readPersonDto(String fixturePath) throws IOException {
    return readPersonDto(fixturePath, true);
  }

  public PersonDto readPersonDto(String fixturePath, boolean useUniqueExternalId)
      throws IOException {
    PersonDto personDto = readObject(fixturePath, PersonDto.class);
    if (useUniqueExternalId) {
      personDto.setExternalId(generateRandomExternalId());
    }
    return personDto;
  }

  public String generateRandomExternalId() {
    return getRandomNumber(random, 8999, 1000)
        + "-"
        + getRandomNumber(random, 8999, 1000)
        + "-"
        + getRandomNumber(random, 8999, 1000)
        + "-"
        + getRandomNumber(random, 8999999, 1000000);
  }

  private String getRandomNumber(Random random, int base, int bound) {
    return String.valueOf(random.nextInt(bound) + base);
  }

  public PersonDto postPerson(PersonDto input, String accountFixture) throws IOException {
    PersonDto person = postPersonAndGetResponse(input, accountFixture).readEntity(PersonDto.class);
    cleanUpPeople.push(person);
    return person;
  }

  public Response postPersonAndGetResponse(PersonDto input, String accountFixture)
      throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
  }

  public String findUserAccountForDelete(CountyDto county) {
    if (county == null) {
      return AUTHORIZED_ACCOUNT_FIXTURE;
    }
    switch (county.getName()) {
      case "El Dorado":
        return AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE;
      case "Marin":
        return AUTHORIZED_ACCOUNT_FIXTURE;
      case "San Luis Obispo":
        return SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED;
      default:
        throw new IllegalArgumentException(
            "There is no account fixture for county: " + county.getName());
    }
  }
}
