package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_ACCOUNT_FIXTURE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE;
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

  private AbstractRestClientTestRule clientTestRule;

  private final Stack<PersonDto> cleanUpPeople = new Stack<>();

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

  public PersonDto postPerson(PersonDto input, boolean useUniqueExternalId) throws IOException {
    if (useUniqueExternalId) {
      input.setExternalId(generateRandomExternalId());
    }
    return postPerson(input, AUTHORIZED_ACCOUNT_FIXTURE);
  }

  public PersonDto postPerson(PersonDto input, String accountFixture, boolean useUniqueExternalId)
      throws IOException {
    if (useUniqueExternalId) {
      input.setExternalId(generateRandomExternalId());
    }
    return postPerson(input, accountFixture);
  }

  public PersonDto postPerson(PersonDto input) throws IOException {
    return postPerson(input, true);
  }

  public Entity<PersonDto> readPersonEntity(String fixturePath) throws IOException {
    return Entity.entity(readPersonDto(fixturePath), MediaType.APPLICATION_JSON_TYPE);
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

  public PersonDto postPerson(String personFixture, String accountFixture) throws IOException {
    final PersonDto person = readPersonDto(personFixture);
    return postPerson(person, accountFixture);
  }

  public Response postPersonAndGetResponse(PersonDto input, String accountFixture)
      throws IOException {
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

  public String findUserAccountForDelete(CountyDto county) {
    if (county == null) {
      return AUTHORIZED_ACCOUNT_FIXTURE;
    }
    switch (county.getName()) {
      case "El Dorado":
        return AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE;
      case "Marin":
        return AUTHORIZED_ACCOUNT_FIXTURE;
      default:
        throw new IllegalArgumentException(
            "There is no account fixture for county: " + county.getName());
    }
  }
}
