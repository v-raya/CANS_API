package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.person.ChildDto;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/** @author CWDS TPT-2 Team */
public class ChildrenResourceTest extends AbstractFunctionalTest {

  private static final String CHILD_CMS_ID = "AbA4BJy0Aq";
  private static final String CHILD_CMS_BASE10_KEY = "0602-0480-3081-8000672";

  @Test
  public void doGetChild_success() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CHILDREN + "/" + CHILD_CMS_ID)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_OK));

    ChildDto childDto = response.readEntity(ChildDto.class);
    Assert.assertEquals(CHILD_CMS_BASE10_KEY, childDto.getExternalId());
  }

  @Test
  public void doGetChild_notFound() throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CHILDREN + "/" + "-1")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
  }
}
