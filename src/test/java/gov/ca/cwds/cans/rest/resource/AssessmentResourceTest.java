package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.INSTRUMENTS;
import static gov.ca.cwds.cans.Constants.API.START;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static gov.ca.cwds.cans.test.util.FixtureReader.readRestObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import gov.ca.cwds.cans.test.util.FixtureReader;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

/** @author denys.davydov */
public class AssessmentResourceTest extends AbstractCrudFunctionalTest<AssessmentDto> {

  private static final String FIXTURE_POST_INSTRUMENT = "fixtures/instrument-post.json";
  private static final String FIXTURE_POST = "fixtures/assessment-post.json";
  private static final String FIXTURE_POST_LOGGING_INFO =
      "fixtures/assessment-post-logging-info.json";
  private static final String FIXTURE_READ = "fixtures/assessment-read.json";
  private static final String FIXTURE_PUT = "fixtures/assessment-put.json";
  private static final String FIXTURE_START = "fixtures/start-assessment-post.json";
  private static final String FIXTURE_EMPTY_OBJECT = "fixtures/empty-object.json";

  private Long tearDownAssessmentId;
  private Long tearDownInstrumentId;

  @After
  public void tearDown() throws IOException {
    if (tearDownAssessmentId != null) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(ASSESSMENTS + SLASH + tearDownAssessmentId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    if (tearDownInstrumentId != null) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(INSTRUMENTS + SLASH + tearDownInstrumentId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    this.cleanUpCreatedUsers();
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
  public void startDemoAssessment_success() throws IOException {
    // given
    final StartAssessmentRequest request = readObject(FIXTURE_START, StartAssessmentRequest.class);
    request.setInstrumentId(1L);

    // when
    final Response postResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + START)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
    final AssessmentDto assessment = postResponse.readEntity(AssessmentDto.class);

    // then
    assertThat(postResponse.getStatus(), is(200));
    this.handleCreationLoggableInstance(assessment);
    assertThat(assessment, is(not(nullValue())));

    // clean up
    this.tearDownAssessmentId = assessment.getId();
  }

  @Test
  public void startAssessment_success() throws IOException {
    // given
    final Entity newInstrument = readRestObject(FIXTURE_POST_INSTRUMENT, InstrumentDto.class);
    tearDownInstrumentId =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(INSTRUMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(newInstrument)
            .readEntity(InstrumentDto.class)
            .getId();
    final Entity<StartAssessmentRequest> startRequest =
        readRestObject(FIXTURE_START, StartAssessmentRequest.class);
    startRequest.getEntity().setInstrumentId(tearDownInstrumentId);

    // when
    final Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + START)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(startRequest);

    // then
    final AssessmentDto actual = response.readEntity(AssessmentDto.class);
    tearDownAssessmentId = actual.getId();
    final AssessmentDto expected = FixtureReader.readObject(FIXTURE_READ, AssessmentDto.class);
    actual.setId(null);
    expected.setId(null);
    expected.setInstrumentId(tearDownInstrumentId);
    this.handleCreationLoggableInstance(actual);
    assertThat(actual, is(expected));
  }

  @Test
  public void startAssessment_failed_whenInvalidInput() throws IOException {
    // given
    final Entity<StartAssessmentRequest> inputEntity =
        readRestObject(FIXTURE_EMPTY_OBJECT, StartAssessmentRequest.class);

    // when
    final Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + START)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(inputEntity);

    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_UNPROCESSABLE_ENTITY));
    final BaseExceptionResponse responsePayload = response.readEntity(BaseExceptionResponse.class);
    assertThat(responsePayload.getIssueDetails().size(), is(2));
  }

  @Test
  public void postAssessment_ignoresInputLogInfo() throws IOException {
    // given
    final AssessmentDto inputAssessment =
        readObject(FIXTURE_POST_LOGGING_INFO, AssessmentDto.class);

    // when
    final AssessmentDto actualAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // then
    assertThat(actualAssessment.getCreatedBy().getId(), is(not(inputAssessment.getCreatedBy().getId())));
    assertThat(actualAssessment.getCreatedTimestamp(), is(not(inputAssessment.getCreatedTimestamp())));
    assertThat(actualAssessment.getUpdatedBy(), is(nullValue()));
    assertThat(actualAssessment.getUpdatedTimestamp(), is(nullValue()));
    assertThat(actualAssessment.getSubmittedBy(), is(nullValue()));
    assertThat(actualAssessment.getSubmittedTimestamp(), is(nullValue()));

    // clean up
    tearDownAssessmentId = actualAssessment.getId();
    this.handleCreationLoggableInstance(actualAssessment);
  }
}
