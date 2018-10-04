package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/** @author denys.davydov */
public class CountyResourceTest extends AbstractFunctionalTest {

  @Test
  public void getAllCounties_success() throws IOException {
    // when
    final CountyDto[] actualResult =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.COUNTIES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(CountyDto[].class);

    // then
    assertThat(actualResult.length, is(not(0)));
  }
}
