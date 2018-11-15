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
import java.util.Stack;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author denys.davydov */
public class AssessmentResourceTest extends AbstractFunctionalTest {

  private static final String AUTHORIZED_MARLIN_ACCOUNT_FIXTURE =
      "fixtures/perry-account/marin-all-authorized.json";
  private static final String FIXTURE_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_COMPLETE_INVALID =
      "fixtures/assessment/assessment-post-complete-fail.json";
  private static final String FIXTURE_POST_NO_AGE_INVALID =
      "fixtures/assessment/assessment-post-no-age-fail.json";
  private static final String FIXTURE_POST_LOGGING_INFO =
      "fixtures/assessment/assessment-post-logging-info.json";
  private static final String CASE_OR_REFERRAL_CMS_ID = "C6vN5DG0Aq";
  private static final String CASE_OR_REFERRAL_CMS_BASE10_KEY = "0687-9473-7673-8000672";
  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();
  private PersonResourceHelper personHelper;

  @Before
  public void before() {
    personHelper = new PersonResourceHelper(clientTestRule);
  }

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(
              personHelper.findUserAccountForDelete(assessmentToDelete.getPerson().getCounty()))
          .target(ASSESSMENTS + SLASH + assessmentToDelete.getId())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    personHelper.cleanUp();
  }

  @Test
  public void postAssessment_ignoresInputLogInfo() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_POST_PERSON, ClientDto.class);
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
    personHelper.pushToCleanUpPerson(actualAssessment.getPerson());
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
    assertThat(itemCodes.size(), is(11));
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
  public void searchAssessments_findsFourSortedRecords() throws IOException {
    // given
    final List<Long> assessmentIds = new ArrayList<>();
    final ClientDto person = readObject(FIXTURE_POST_PERSON, ClientDto.class);
    final ClientDto otherPerson = readObject(FIXTURE_POST_PERSON, ClientDto.class);
    otherPerson.setIdentifier("aaaaaaaaaa");
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    final List<Object[]> properties =
        Arrays.asList(
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2010, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2015, 10, 10), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            // out of search results because of the other person
            new Object[] {
              otherPerson,
              IN_PROGRESS,
              LocalDate.of(2015, 10, 10),
              AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, COMPLETED, LocalDate.of(2010, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
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
        personHelper.pushToCleanUpPerson(newAssessment.getPerson());
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
  public void getChangeLog_findsFourSortedRecords() throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_POST_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    final List<Object[]> properties =
        Arrays.asList(
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2018, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2018, 2, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, IN_PROGRESS, LocalDate.of(2018, 5, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
              person, COMPLETED, LocalDate.of(2018, 10, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
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
        cleanUpAssessments.push(newAssessment);
        personHelper.pushToCleanUpPerson(newAssessment.getPerson());

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
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
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
    final ClientDto person = readObject(FIXTURE_POST_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    assessment.setServiceSource(ServiceSource.CASE);
    assessment.setServiceSourceId(CASE_OR_REFERRAL_CMS_ID);
    final AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // when
    postedAssessment.setCounty((CountyDto) new CountyDto().setName("Sacramento").setId(1L));
    assessment.setServiceSourceId("otherId000");
    postedAssessment.setConductedBy("John Smith");
    final AssessmentDto actualAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // then
    assertThat(actualAssessment.getCounty().getId(), is(9L));
    assertThat(actualAssessment.getServiceSource(), is(ServiceSource.CASE));
    assertThat(actualAssessment.getServiceSourceId(), is(CASE_OR_REFERRAL_CMS_ID));
    assertThat(actualAssessment.getServiceSourceUiId(), is(CASE_OR_REFERRAL_CMS_BASE10_KEY));
    assertThat(actualAssessment.getConductedBy(), is("John Smith"));
    // clean up
    personHelper.pushToCleanUpPerson(postedAssessment.getPerson());
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void putAssessment_validationError_whenUpdatingConductedByOnCompleted()
      throws IOException {
    // given
    final ClientDto person = readObject(FIXTURE_POST_PERSON, ClientDto.class);
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
    personHelper.pushToCleanUpPerson(postedAssessment.getPerson());
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void putAssessment_unauthorized_whenUserFromDifferentCounty() throws IOException {
    // given
    final ClientDto personElDoradoCounty = readObject(FIXTURE_POST_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(personElDoradoCounty);
    final AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);

    // when
    final int status =
        clientTestRule
            .withSecurityToken(AUTHORIZED_MARLIN_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE))
            .getStatus();

    // then
    assertThat(status, is(403));

    // clean up
    personHelper.pushToCleanUpPerson(postedAssessment.getPerson());
    cleanUpAssessments.push(postedAssessment);
  }

  /* Authorization will be reworked

  @Test
  public void getAssessment_authorized_whenUserHasSealedAndClientIsSealed() throws IOException {
    // given
    final PersonDto personDto = personHelper.readPersonDto(FIXTURE_POST_PERSON);
    personDto.setSensitivityType(SensitivityType.SEALED);
    final PersonDto person =
        personHelper.postPerson(personDto, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
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
    final int status =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(status, is(200));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void getAssessment_unauthorized_whenUserHasSealedAndClientIsSealedButDifferentCounty()
      throws IOException {
    // given
    final PersonDto personDto = personHelper.readPersonDto(FIXTURE_POST_PERSON);
    personDto.setSensitivityType(SensitivityType.SEALED);
    final PersonDto person =
        personHelper.postPerson(personDto, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
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
    final int status =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(status, is(403));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void getAssessment_unauthorized_whenUserHasNotSealedAndClientIsSealed()
      throws IOException {
    // given
    final Entity<PersonDto> personEntity = personHelper.readPersonEntity(FIXTURE_POST_PERSON);
    personEntity.getEntity().setSensitivityType(SensitivityType.SEALED);
    final PersonDto person =
        personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
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
    final int status =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + postedAssessment.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(status, is(403));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    cleanUpAssessments.push(postedAssessment);
  }*/

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
