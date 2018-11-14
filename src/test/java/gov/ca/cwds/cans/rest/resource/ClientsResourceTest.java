package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/** @author CWDS TPT-2 Team */
public class ClientsResourceTest extends AbstractFunctionalTest {

  private static final String CLIENT_CMS_ID = "AbA4BJy0Aq";
  private static final String CLIENT_CMS_BASE10_KEY = "0602-0480-3081-8000672";
  private static final String CASE_OR_REFERRAL_CMS_ID = "C6vN5DG0Aq";
  private static final String CASE_OR_REFERRAL_CMS_BASE10_KEY = "0687-9473-7673-8000672";

  @Test
  public void doGetClient_success() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CLIENTS + SLASH + CLIENT_CMS_ID)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_OK));

    ClientDto clientDto = response.readEntity(ClientDto.class);
    Assert.assertEquals(CLIENT_CMS_BASE10_KEY, clientDto.getExternalId());
    Assert.assertEquals(CASE_OR_REFERRAL_CMS_ID, clientDto.getServiceSourceId());
    Assert.assertEquals(CASE_OR_REFERRAL_CMS_BASE10_KEY, clientDto.getServiceSourceUiId());
    Assert.assertEquals(ServiceSource.CASE, clientDto.getServiceSource());
  }

  @Test
  public void doGetClient_notFound() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CLIENTS + SLASH + "-1")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
  }
}
