package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.START;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import liquibase.exception.LiquibaseException;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author denys.davydov */
public class AssessmentResourceTest extends AbstractCrudIntegrationTest<AssessmentDto> {

  private static final String LIQUIBASE_SCRIPT = "liquibase/instrument_insert.xml";

  private static final String FIXTURE_POST = "fixtures/assessment-post.json";
  private static final String FIXTURE_READ = "fixtures/assessment-read.json";
  private static final String FIXTURE_PUT = "fixtures/assessment-put.json";
  private static final String FIXTURE_START = "fixtures/start-assessment-post.json";
  private static final String FIXTURE_EMPTY_OBJECT = "fixtures/empty-object.json";

  private Long tearDownAssessmentId;

  @BeforeClass
  public static void onBeforeClass() throws LiquibaseException {
    DATABASE_HELPER_CANS.runScripts(LIQUIBASE_SCRIPT);
  }

  @AfterClass
  public static void onAfterClass() throws LiquibaseException {
    DATABASE_HELPER_CANS.rollbackScripts(LIQUIBASE_SCRIPT);
  }

  @After
  public void tearDown() throws IOException {
    if (tearDownAssessmentId != null) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(ASSESSMENTS + SLASH + tearDownAssessmentId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Override
  String getPostFixturePath() {
    return FIXTURE_POST;
  }

  @Override
  String getPutFixturePath() {
    return FIXTURE_PUT;
  }

  @Override
  String getApiPath() {
    return ASSESSMENTS;
  }

  @Test
  public void assessment_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }

  @Test
  public void startAssessment_success() throws IOException {
    // given
    final Entity<StartAssessmentRequest> inputEntity = restEntityByFixture(FIXTURE_START);

    // when
    final Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + START)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(inputEntity);

    // then
    final AssessmentDto actual = response.readEntity(AssessmentDto.class);
    tearDownAssessmentId = actual.getId();
    actual.setId(null);
    final String expectedFixture = FixtureHelpers.fixture(FIXTURE_READ);
    final AssessmentDto expected = OBJECT_MAPPER.readValue(expectedFixture, AssessmentDto.class);
    assertThat(actual, is(expected));
  }

  @Test
  public void startAssessment_failed_whenInvalidInput() throws IOException {
    // given
    final Entity<StartAssessmentRequest> inputEntity = restEntityByFixture(FIXTURE_EMPTY_OBJECT);

    // when
    final Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + START)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(inputEntity);

    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_UNPROCESSABLE_ENTITY));
    final BaseExceptionResponse responsePayload = response
        .readEntity(BaseExceptionResponse.class);
    assertThat(responsePayload.getIssueDetails().size(), is(2));
  }

  private Entity<StartAssessmentRequest> restEntityByFixture(String fixture)
      throws IOException {
    final String inputFixture = FixtureHelpers.fixture(fixture);
    final Class<StartAssessmentRequest> valueType = StartAssessmentRequest.class;
    final StartAssessmentRequest request =
        OBJECT_MAPPER.readValue(inputFixture, valueType);
    return Entity.entity(request, MediaType.APPLICATION_JSON_TYPE);
  }
}
