package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.ConstructDto;
import gov.ca.cwds.cans.rest.AbstractIntegrationTest;
import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;

/** @author denys.davydov */
public class ConstructResourceTest extends AbstractIntegrationTest {

  @Test
  public void postAndDeleteConstruct_success() throws IOException {
    // POST
    // given
    final String fixture = FixtureHelpers.fixture("fixtures/construct/post-construct.json");
    final ConstructDto construct =
        ObjectMapperUtils.createObjectMapper().readValue(fixture, ConstructDto.class);

    final Long constructId = assertPostOperation(construct);

    // READ
    // when
    final ConstructDto getConstructResult =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CONSTRUCTS + SLASH + constructId)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(ConstructDto.class);

    // then
    getConstructResult.setId(null);
    assertThat(getConstructResult, is(construct));

    // DELETE
    // when
    clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.CONSTRUCTS + SLASH + constructId)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .delete();

    final Response getConstructSecondResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CONSTRUCTS + SLASH + constructId)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

    // then
    assertThat(getConstructSecondResponse.getStatus(), is(HttpStatus.SC_NOT_FOUND));
  }

  private Long assertPostOperation(ConstructDto construct) throws IOException {
    // given
    final Entity<ConstructDto> inputConstruct =
        Entity.entity(construct, MediaType.APPLICATION_JSON_TYPE);

    // when
    final Response postResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.CONSTRUCTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(inputConstruct);

    final ConstructDto actualResult = postResponse.readEntity(ConstructDto.class);

    // then
    final Long constructId = actualResult.getId();
    assertThat(constructId, is(not(nullValue())));
    actualResult.setId(null);
    assertThat(actualResult, is(construct));
    return constructId;
  }
}
