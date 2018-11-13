package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.test.util.FixtureReader;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author CWDS TPT-2 Team
 */
public class ClientsResourceTest extends AbstractFunctionalTest {

  private static final String AUTHORIZED_USER = "fixtures/perry-account/0ki-napa-all.json";
  private static final String CLIENT = "fixtures/client-of-0Ki-rw-assignment.json";


  @Test
  public void doGetClient_success() throws IOException {
    ClientDto expected = FixtureReader
        .readObject(CLIENT, ClientDto.class);
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_USER)
            .target(API.CLIENTS + "/" + expected.getIdentifier())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_OK));

    ClientDto clientDto = response.readEntity(ClientDto.class);
    Assert.assertEquals(expected.getExternalId(), clientDto.getExternalId());
  }

  @Test
  public void doGetClient_notFound() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_USER)
            .target(API.CLIENTS + "/" + "-1")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
  }
}
