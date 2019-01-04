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
import static org.hamcrest.Matchers.notNullValue;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.dto.changelog.AssessmentChangeLogDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentChangeType;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
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
  private static final String CASE_OR_REFERRAL_CMS_ID = "C6vN5DG0Aq";
  private static final String CASE_OR_REFERRAL_CMS_BASE10_KEY = "0687-9473-7673-8000672";

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
    pushToCleanUpStack(actualAssessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);

    // then
    assertThat(
        actualAssessment.getCreatedBy().getId(), is(not(inputAssessment.getCreatedBy().getId())));
    assertThat(
        actualAssessment.getCreatedTimestamp(), is(not(inputAssessment.getCreatedTimestamp())));
    assertThat(actualAssessment.getUpdatedBy(), is(nullValue()));
    assertThat(actualAssessment.getUpdatedTimestamp(), is(not(nullValue())));
    assertThat(actualAssessment.getCompletedBy(), is(nullValue()));
    assertThat(actualAssessment.getCompletedTimestamp(), is(nullValue()));
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
    final Set<String> itemCodes =
        exceptionResponse
            .getIssueDetails()
            .stream()
            .map(IssueDetails::getProperty)
            .collect(Collectors.toSet());
    assertThat(itemCodes.size(), is(16));
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
            "state.domains.code",
            "state.domains.caregiverName",
            "state.domains.comment",
            "state.domains.items.code",
            "state.domains.items.comment",
            "state.domains.items.ratingType",
            "person"));
  }

  @Test
  public void postAssessment_failed_whenSubmittingInProgressWithNoAge() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_POST_PERSON, ClientDto.class);
    final AssessmentDto inputAssessment =
        readObject(FIXTURE_POST_NO_AGE_INVALID, AssessmentDto.class);
    inputAssessment.setPerson(person);

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
    assertThat(itemCodes.size(), is(1));
    assertThat(itemCodes, containsInAnyOrder("state.under_six"));
  }

  @Test
  public void deleteAssessment_returns404_whenTryingToGetDeletedAssessment() throws IOException {
    // given
    final AssessmentDto inputAssessment =
        (AssessmentDto)
            readObject(FIXTURE_POST_LOGGING_INFO, AssessmentDto.class)
                .setPerson(readObject(FIXTURE_PERSON, ClientDto.class));
    final AssessmentDto assessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    pushToCleanUpStack(assessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);

    // when
    final AssessmentMetaDto deletedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + assessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .delete()
            .readEntity(AssessmentMetaDto.class);

    // then
    assertThat(deletedAssessment.getStatus(), is(AssessmentStatus.DELETED));
    final Response getResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + assessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    assertThat(getResponse.getStatus(), is(HttpStatus.SC_NOT_FOUND));
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
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2010, 1, 1), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2015, 10, 10), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            // out of search results because of the other person
            new Object[] {
              otherPerson, IN_PROGRESS, LocalDate.of(2015, 10, 10), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, COMPLETED, LocalDate.of(2010, 1, 1), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, COMPLETED, LocalDate.of(2015, 10, 10), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
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
        pushToCleanUpStack(newAssessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);
      }
    }
    // when
    final AssessmentMetaDto[] actualResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(toSearchAssessmentRequestEntity(person.getIdentifier(), false))
            .readEntity(AssessmentMetaDto[].class);

    // then
    assertThat(actualResults.length, is(4));
    assertThat(actualResults[0].getId(), is(assessmentIds.get(1)));
    assertThat(actualResults[1].getId(), is(assessmentIds.get(0)));
    assertThat(actualResults[2].getId(), is(assessmentIds.get(4)));
    assertThat(actualResults[3].getId(), is(assessmentIds.get(3)));

    // check allowed operations

    // first two are in progress, so "complete" permission is present
    for (int i = 0; i < 2; i++) {
      AssessmentMetaDto metaDto = actualResults[i];
      checkOperations(metaDto, "read", "update", "create", "complete", "write", "delete");
    }

    // next two are completed so "complete" permission is absent
    for (int i = 2; i < 4; i++) {
      AssessmentMetaDto metaDto = actualResults[i];
      checkOperations(metaDto, "read", "update", "create", "write", "delete");
    }
  }

  @Test
  public void searchAssessments_returnsNoSoftDeletedRecords_whenIncludeDeletedIsFalse()
      throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto inputAssessment =
        (AssessmentDto)
            readObject(FIXTURE_POST_LOGGING_INFO, AssessmentDto.class).setPerson(person);
    final AssessmentDto assessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    clientTestRule
        .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + assessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .delete();

    // when
    final AssessmentMetaDto[] actualResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(toSearchAssessmentRequestEntity(person.getIdentifier(), false))
            .readEntity(AssessmentMetaDto[].class);

    // then
    assertThat(actualResults.length, is(0));
  }

  @Test
  public void searchAssessments_returnsSoftDeletedRecords_whenIncludeDeletedIsTrue()
      throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto inputAssessment =
        (AssessmentDto)
            readObject(FIXTURE_POST_LOGGING_INFO, AssessmentDto.class).setPerson(person);
    final AssessmentDto assessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    clientTestRule
        .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + assessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .delete();

    // when
    final AssessmentMetaDto[] searchResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + SEARCH)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(toSearchAssessmentRequestEntity(person.getIdentifier(), true))
            .readEntity(AssessmentMetaDto[].class);

    // then
    final AssessmentMetaDto expectedAssessment =
        Arrays.stream(searchResults)
            .filter(item -> item.getId().equals(assessment.getId()))
            .findFirst()
            .orElse(null);
    assertThat(expectedAssessment, is(notNullValue()));
    assertThat(expectedAssessment.getStatus(), is(AssessmentStatus.DELETED));
  }

  @Test
  public void getChangeLog_findsFourSortedRecords() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    final List<Object[]> properties =
        Arrays.asList(
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2018, 1, 1), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2018, 2, 1), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2018, 5, 1), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, COMPLETED, LocalDate.of(2018, 10, 1), AUTHORIZED_NAPA_ACCOUNT_FIXTURE
            });
    Long id = null;
    for (Object[] property : properties) {
      final AssessmentDto newAssessment;
      if (id == null) {
        newAssessment =
            postAssessment(
                assessment,
                (ClientDto) property[0],
                (AssessmentStatus) property[1],
                (LocalDate) property[2],
                (String) property[3]);

        id = newAssessment.getId();
        pushToCleanUpStack(newAssessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);

      } else {
        newAssessment =
            putAssessment(
                assessment,
                (ClientDto) property[0],
                (AssessmentStatus) property[1],
                (LocalDate) property[2],
                (String) property[3],
                id);
      }
      if (newAssessment.getId() != null) {}
    }
    // when
    final AssessmentChangeLogDto[] actualResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + String.valueOf(id) + SLASH + CHANGELOG)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(AssessmentChangeLogDto[].class);

    // then
    assertThat(actualResults.length, is(4));
    assertThat(actualResults[0].getEntityId(), is(id));
    assertThat(actualResults[1].getEntityId(), is(id));
    assertThat(actualResults[2].getEntityId(), is(id));
    assertThat(actualResults[3].getEntityId(), is(id));

    assertThat(actualResults[0].getAssessmentChangeType(), is(AssessmentChangeType.COMPLETED));
    assertThat(actualResults[1].getAssessmentChangeType(), is(AssessmentChangeType.SAVED));
    assertThat(actualResults[2].getAssessmentChangeType(), is(AssessmentChangeType.SAVED));
    assertThat(actualResults[3].getAssessmentChangeType(), is(AssessmentChangeType.CREATED));
  }

  @Test
  public void putAssessment_notUpdatingCountyAndCaseId_whenUpdatingAssessment() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    assessment.setServiceSource(ServiceSource.CASE);
    assessment.setServiceSourceId(CASE_OR_REFERRAL_CMS_ID);
    final AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    pushToCleanUpStack(postedAssessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);

    // when
    postedAssessment.setCounty((CountyDto) new CountyDto().setName("Sacramento").setId(1L));
    assessment.setServiceSourceId("otherId000");
    postedAssessment.setConductedBy("John Smith");
    final AssessmentDto actualAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // then
    assertThat(actualAssessment.getCounty().getId(), is(28L));
    assertThat(actualAssessment.getServiceSource(), is(ServiceSource.CASE));
    assertThat(actualAssessment.getServiceSourceId(), is(CASE_OR_REFERRAL_CMS_ID));
    assertThat(actualAssessment.getServiceSourceUiId(), is(CASE_OR_REFERRAL_CMS_BASE10_KEY));
    assertThat(actualAssessment.getConductedBy(), is("John Smith"));
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
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    pushToCleanUpStack(postedAssessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);
    // when

    postedAssessment.setConductedBy("Other Person");
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_UNPROCESSABLE_ENTITY));
  }

  @Test
  public void getAssessment_success_whenExistingCans() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    AssessmentDto postedAssessment =
        postAssessment(
            assessment,
            person,
            AssessmentStatus.IN_PROGRESS,
            LocalDate.now(),
            AUTHORIZED_NAPA_ACCOUNT_FIXTURE);
    pushToCleanUpStack(postedAssessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);
    // when
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getAssessment_notFound_whenNonExistingCans() throws IOException {
    // given
    final String assessmentId = "1234567890";
    // when
    Response response =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + assessmentId)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

    // then
    assertThat(response.getStatus(), is(HttpStatus.SC_NOT_FOUND));
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

  private Entity<SearchAssessmentRequest> toSearchAssessmentRequestEntity(
      String personIdentifier, boolean shouldIncludeDeleted) {
    return Entity.entity(
        new SearchAssessmentRequest()
            .setClientIdentifier(personIdentifier)
            .setIncludeDeleted(shouldIncludeDeleted),
        MediaType.APPLICATION_JSON_TYPE);
  }
}
