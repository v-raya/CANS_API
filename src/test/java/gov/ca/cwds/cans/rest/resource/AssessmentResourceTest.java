package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.COMPLETED;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.IN_PROGRESS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author denys.davydov
 */
public class AssessmentResourceTest extends AbstractFunctionalTest {

  private static final String FIXTURE_PERSON = "fixtures/client-of-0Ki-rw-assignment.json";
  private static final String FIXTURE_POST = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_COMPLETE_INVALID =
      "fixtures/assessment/assessment-post-complete-fail.json";
  private static final String FIXTURE_POST_LOGGING_INFO =
      "fixtures/assessment/assessment-post-logging-info.json";
  private static final String AUTHORIZED_USER =
      "fixtures/perry-account/0ki-napa-all.json";
  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(
              AUTHORIZED_USER)
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
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
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
    assertThat(itemCodes.size(), is(9));
    assertThat(
        itemCodes,
        containsInAnyOrder(
            "item.code3",
            "can_release_confidential_info",
            "assessment_type",
            "has_caregiver",
            "event_date",
            "completed_as",
            "state.is_under_six",
            "state.domains.caregiverName",
            "person"));
  }

  @Test
  public void searchAssessments_findsFourSortedRecords() throws IOException {
    // given
    final List<Long> assessmentIds = new ArrayList<>();
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final ClientDto otherPerson = readObject(FIXTURE_PERSON, ClientDto.class);
    otherPerson.setIdentifier("aaaaaaaaaa");
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    final List<Object[]> properties =
        Arrays.asList(
            new Object[]{
                person, IN_PROGRESS, LocalDate.of(2010, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[]{
                person, IN_PROGRESS, LocalDate.of(2015, 10, 10),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            // out of search results because of the other person
            new Object[]{
                otherPerson,
                IN_PROGRESS,
                LocalDate.of(2015, 10, 10),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[]{
                person, COMPLETED, LocalDate.of(2010, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[]{
                person, COMPLETED, LocalDate.of(2015, 10, 10), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            }
            /*, Authorization going to be reworked
            // out of search results because of the other created by user
            new Object[] {
              person, COMPLETED, LocalDate.of(2015, 10, 10), NOT_AUTHORIZED_ACCOUNT_FIXTURE
            }*/
        );

    for (Object[] property : properties) {
      final AssessmentDto newAssessment =
          postAssessment(
              assessment,
              (ClientDto) property[0],
              (AssessmentStatus) property[1],
              (LocalDate) property[2],
              (String) property[3]);
      assessmentIds.add(newAssessment.getId());
      if (newAssessment.getId() != null) {
        cleanUpAssessments.push(newAssessment);
      }
    }
    // when
    final Entity<SearchAssessmentRequest> searchRequest =
        Entity.entity(
            new SearchAssessmentRequest().setClientIdentifier(person.getIdentifier()),
            MediaType.APPLICATION_JSON_TYPE);
    final AssessmentMetaDto[] actualResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(searchRequest)
            .readEntity(AssessmentMetaDto[].class);

    // then
    assertThat(actualResults.length, is(4));
    assertThat(actualResults[0].getId(), is(assessmentIds.get(1)));
    assertThat(actualResults[1].getId(), is(assessmentIds.get(0)));
    assertThat(actualResults[2].getId(), is(assessmentIds.get(4)));
    assertThat(actualResults[3].getId(), is(assessmentIds.get(3)));
  }

  @Test
  public void putAssessment_notUpdatingCounty_whenUpdatingAssessment() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    final AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // when
    postedAssessment.setCounty((CountyDto) new CountyDto().setId(1L));
    postedAssessment.getCounty().setName("Sacramento");
    postedAssessment.setConductedBy("John Smith");
    final AssessmentDto actualAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // then
    assertThat(actualAssessment.getCounty().getId(), is(28L));
    assertThat(actualAssessment.getConductedBy(), is("John Smith"));
    // clean up
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void putAssessment_validationError_whenUpdatingConductedByOnCompleted()
      throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    assessment.setConductedBy("John Smith");
    assessment.setStatus(AssessmentStatus.COMPLETED);
    final AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // when

    postedAssessment.setConductedBy("Other Person");
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
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
}
