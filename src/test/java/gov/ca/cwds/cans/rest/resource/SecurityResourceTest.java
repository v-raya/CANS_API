package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CHECK_PERMISSION;
import static gov.ca.cwds.cans.Constants.API.SECURITY;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;

import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Test;

public class SecurityResourceTest extends AbstractFunctionalTest {

  private static final String PERSON_FIXTURE = "fixtures/client-of-0Ki-rw-assignment.json";
  private static final String AUTHORIZED_USER = "fixtures/perry-account/0ki-napa-all.json";
  private static final String UNAUTHORIZED_USER = "fixtures/perry-account/marlin-unauthorized.json";

  @Test
  public void testAuthorized() throws Exception {
    PersonDto personDto = readObject(PERSON_FIXTURE, PersonDto.class);
    final Boolean authorized =
        clientTestRule
            .withSecurityToken(AUTHORIZED_USER)
            .target(SECURITY + "/" + CHECK_PERMISSION + "/client:read:" + personDto.getIdentifier())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(Boolean.class);
    Assert.assertTrue(authorized);
  }

  @Test
  public void testUnauthorized() throws Exception {
    PersonDto personDto = readObject(PERSON_FIXTURE, PersonDto.class);
    final Boolean authorized =
        clientTestRule
            .withSecurityToken(UNAUTHORIZED_USER)
            .target(SECURITY + "/" + CHECK_PERMISSION + "/client:read:" + personDto.getIdentifier())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(Boolean.class);
    Assert.assertFalse(authorized);
  }
}
