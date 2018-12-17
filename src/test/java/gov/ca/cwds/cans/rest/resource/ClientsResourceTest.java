package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import gov.ca.cwds.cans.test.util.FixtureReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
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
  private static final String AUTHORIZED_USER_NAPA = "fixtures/perry-account/0ki-napa-all.json";
  private static final String UNAUTHORIZED_USER = AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE;
  private static final String SEALED_CLIENT_MARLIN = "fixtures/sealed-client-marlin.json";
  private static final String AUTHORIZED_ACCOUNT =
      "fixtures/perry-account/1126-all-authorized.json";

  @Test
  public void doGetClient_success() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT)
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
  public void doGetClient_allowedOperationsArePresent() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT)
            .target(API.CLIENTS + SLASH + CLIENT_CMS_ID)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_OK));

    ClientDto clientDto = response.readEntity(ClientDto.class);
    checkOperations(clientDto, "read", "createAssessment", "completeAssessment");
  }

  @Test
  public void doGetClient_unauthorized() throws IOException {
    ClientDto expected = FixtureReader.readObject(SEALED_CLIENT_MARLIN, ClientDto.class);
    Response response =
        clientTestRule
            .withSecurityToken(UNAUTHORIZED_USER)
            .target(API.CLIENTS + "/" + expected.getIdentifier())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void doGetClient_notFound() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_USER_NAPA)
            .target(API.CLIENTS + SLASH + "-1")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
  }

  private void checkOperations(ClientDto assessmentDto, String... operations) {
    final String opsKey = "allowed_operations";
    Assert.assertEquals(
        new TreeSet<>(Arrays.asList(operations)),
        new TreeSet<>((Collection<?>) assessmentDto.getMetadata().get(opsKey)));
  }
}
