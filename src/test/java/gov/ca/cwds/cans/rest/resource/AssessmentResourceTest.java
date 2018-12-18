package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.CHANGELOG;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.COMPLETED;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.IN_PROGRESS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

/** @author denys.davydov */
public class AssessmentResourceTest extends AbstractFunctionalTest {

  private static final String FIXTURE_PERSON = "fixtures/client-of-0Ki-rw-assignment.json";
  private static final String FIXTURE_POST = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST_COMPLETE_INVALID =
      "fixtures/assessment/assessment-post-complete-fail.json";
  private static final String FIXTURE_POST_NO_AGE_INVALID =
      "fixtures/assessment/assessment-post-no-age-fail.json";
  private static final String FIXTURE_POST_LOGGING_INFO =
      "fixtures/assessment/assessment-post-logging-info.json";
  private static final String AUTHORIZED_USER = "fixtures/perry-account/0ki-napa-all.json";
  private static final String CASE_OR_REFERRAL_CMS_ID = "C6vN5DG0Aq";
  private static final String CASE_OR_REFERRAL_CMS_BASE10_KEY = "0687-9473-7673-8000672";
  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(AUTHORIZED_USER)
          .target(ASSESSMENTS + SLASH + assessmentToDelete.getId())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Test
  public void postAssessment_ignoresInputLogInfo() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto inputAssessment =
        readObject(FIXTURE_POST_LOGGING_INFO, AssessmentDto.class);
    inputAssessment.setPerson(person);

    // when
    final AssessmentDto actualAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // then
    assertThat(
        actualAssessment.getCreatedBy().getId(), is(not(inputAssessment.getCreatedBy().getId())));
    assertThat(
        actualAssessment.getCreatedTimestamp(), is(not(inputAssessment.getCreatedTimestamp())));
    assertThat(actualAssessment.getUpdatedBy(), is(nullValue()));
    assertThat(actualAssessment.getUpdatedTimestamp(), is(nullValue()));
    assertThat(actualAssessment.getCompletedBy(), is(nullValue()));
    assertThat(actualAssessment.getCompletedTimestamp(), is(nullValue()));

    // clean up
    cleanUpAssessments.push(actualAssessment);
  }

  @Test
  public void postAssessment_failed_whenSubmittingInvalid() throws IOException {
    // given
    final AssessmentDto inputAssessment =
        readObject(FIXTURE_POST_COMPLETE_INVALID, AssessmentDto.class);

    // when
    final Response postResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertThat(postResponse.getStatus(), is(HttpStatus.SC_UNPROCESSABLE_ENTITY));
    final BaseExceptionResponse exceptionResponse =
        postResponse.readEntity(BaseExceptionResponse.class);
    final List<String> itemCodes =
        exceptionResponse
            .getIssueDetails()
            .stream()
            .map(IssueDetails::getProperty)
            .collect(Collectors.toList());
    assertThat(itemCodes.size(), is(13));
    assertThat(
        itemCodes,
        containsInAnyOrder(
            "item.code3",
            "can_release_confidential_info",
            "assessment_type",
            "serviceSourceId",
            "service_source",
            "has_caregiver",
            "event_date",
            "completed_as",
            "state.under_six",
            "state.domains.caregiverName",
            "state.domains.comment",
            "state.domains.items.comment",
            "person"));
  }

  @Test
  public void putAssessment1_validationError_whenUpdatingConductedByOnCompleted()
      throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    assessment.setConductedBy("John Smith");
    assessment.setStatus(AssessmentStatus.COMPLETED);
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE));
    assertThat(response.getStatus(), is(HttpStatus.SC_CREATED));
    final AssessmentDto postedAssessment = response.readEntity(AssessmentDto.class);

    // when
    postedAssessment.setConductedBy("Other Person");
    response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_UNPROCESSABLE_ENTITY));

    // clean up
    cleanUpAssessments.push(postedAssessment);
  }

  private AssessmentDto postAssessment(
      AssessmentDto assessment,
      ClientDto person,
      AssessmentStatus status,
      LocalDate eventDate,
      String perryUserFixture)
      throws IOException {
    assessment.setPerson(person);
    assessment.setStatus(status);
    assessment.setEventDate(eventDate);
    return clientTestRule
        .withSecurityToken(perryUserFixture)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);
  }

  private AssessmentDto putAssessment(
      AssessmentDto assessment,
      ClientDto person,
      AssessmentStatus status,
      LocalDate eventDate,
      String perryUserFixture,
      Long id)
      throws IOException {
    assessment.setId(id);
    assessment.setPerson(person);
    assessment.setStatus(status);
    assessment.setEventDate(eventDate);
    return clientTestRule
        .withSecurityToken(perryUserFixture)
        .target(ASSESSMENTS + SLASH + String.valueOf(id))
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);
  }
}
