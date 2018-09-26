package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.IN_PROGRESS;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.SUBMITTED;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static gov.ca.cwds.cans.test.util.FixtureReader.readRestObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
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

  private static final String AUTHORIZED_MARLIN_ACCOUNT_FIXTURE =
      "fixtures/perry-account/marin-all-authorized.json";
  private static final String FIXTURE_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_SUBMIT_INVALID = "fixtures/assessment/assessment-post-submit-fail.json";
  private static final String FIXTURE_POST_LOGGING_INFO =
      "fixtures/assessment/assessment-post-logging-info.json";
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
    final PersonDto person = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
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
        actualAssessment.getCreatedBy().getId(),
        is(not(inputAssessment.getCreatedBy().getId()))
    );
    assertThat(
        actualAssessment.getCreatedTimestamp(),
        is(not(inputAssessment.getCreatedTimestamp()))
    );
    assertThat(actualAssessment.getUpdatedBy(), is(nullValue()));
    assertThat(actualAssessment.getUpdatedTimestamp(), is(nullValue()));
    assertThat(actualAssessment.getSubmittedBy(), is(nullValue()));
    assertThat(actualAssessment.getSubmittedTimestamp(), is(nullValue()));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    cleanUpAssessments.push(actualAssessment);

  }

  @Test
  public void postAssessment_failed_whenSubmittingInvalid() throws IOException {
    // given
    final AssessmentDto inputAssessment =
        readObject(FIXTURE_POST_SUBMIT_INVALID, AssessmentDto.class);

    // when
    final Response postResponse = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(inputAssessment, MediaType.APPLICATION_JSON_TYPE));

    // then
    assertThat(postResponse.getStatus(), is(HttpStatus.SC_UNPROCESSABLE_ENTITY));
    final BaseExceptionResponse exceptionResponse = postResponse
        .readEntity(BaseExceptionResponse.class);
    final List<String> itemCodes = exceptionResponse.getIssueDetails().stream()
        .map(IssueDetails::getProperty)
        .collect(Collectors.toList());
    assertThat(itemCodes.size(), is(9));
    assertThat(itemCodes, containsInAnyOrder(
        "item.code3",
        "can_release_confidential_info",
        "assessment_type",
        "has_caregiver",
        "event_date",
        "completed_as",
        "state.is_under_six",
        "state.domains.caregiverName",
        "person"
    ));
  }

  @Test
  public void searchAssessments_findsFourSortedRecords() throws IOException {
    // given
    final List<Long> assessmentIds = new ArrayList<>();
    final PersonDto person = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final PersonDto otherPerson = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);

    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    final List<Object[]> properties = Arrays.asList(
        new Object[]{person, IN_PROGRESS, LocalDate.of(2010, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE},
        new Object[]{person, IN_PROGRESS, LocalDate.of(2015, 10, 10),
            AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE},
        // out of search results because of the other person
        new Object[]{otherPerson, IN_PROGRESS, LocalDate.of(2015, 10, 10),
            AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE},
        new Object[]{person, SUBMITTED, LocalDate.of(2010, 1, 1), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE},
        new Object[]{person, SUBMITTED, LocalDate.of(2015, 10, 10), AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE},
        // out of search results because of the other created by user
        new Object[]{person, SUBMITTED, LocalDate.of(2015, 10, 10), NOT_AUTHORIZED_ACCOUNT_FIXTURE}
    );

    for (Object[] property : properties) {
      final AssessmentDto newAssessment = postAssessment(
          assessment,
          (PersonDto) property[0],
          (AssessmentStatus) property[1],
          (LocalDate) property[2],
          (String) property[3]
      );
      assessmentIds.add(newAssessment.getId());
      if (newAssessment.getId() != null) {
        cleanUpAssessments.push(newAssessment);
      }
    }
    // when
    final Entity<SearchAssessmentRequest> searchRequest = Entity.entity(
        new SearchAssessmentRequest().setPersonId(person.getId()),
        MediaType.APPLICATION_JSON_TYPE
    );
    final AssessmentMetaDto[] actualResults = clientTestRule
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

    // clean up
    personHelper.pushToCleanUpPerson(person);
    personHelper.pushToCleanUpPerson(otherPerson);
  }

  @Test
  public void putAssessment_assessmentCaseNumberUpdated_whenPersonsCaseNumberUpdated()
      throws IOException {
    // given
    final PersonDto person0 = personHelper.readPersonDto(FIXTURE_POST_PERSON);
    person0.getCases().get(0).setExternalId("4321-321-4321-87654321");
    final PersonDto postedPerson0 = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(person0, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(PersonDto.class);

    final PersonDto person = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    assessment.setTheCase(person.getCases().get(0));
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // when
    person.getCases().get(0).setExternalId("2222-222-3333-44444444");
    person.getCases()
        .add((CaseDto) new CaseDto().setExternalId("4321-321-4321-87654321").setId(123L));
    clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(PEOPLE + SLASH + person.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE));

    // then
    final AssessmentDto updatedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + postedAssessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get()
        .readEntity(AssessmentDto.class);
    assertThat(updatedAssessment.getTheCase().getExternalId(), is("2222-222-3333-44444444"));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    personHelper.pushToCleanUpPerson(postedPerson0);
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void putAssessment_notUpdatingCounty_whenUpdatingAssessment() throws IOException {
    // given
    final PersonDto person = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // when
    postedAssessment.setCounty((CountyDto) new CountyDto().setId(1L));
    postedAssessment.getCounty().setName("Sacramento");
    final AssessmentDto actualAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + postedAssessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // then
    assertThat(actualAssessment.getCounty().getId(), is(9L));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void putAssessment_unauthorized_whenUserFromDifferentCounty() throws IOException {
    // given
    final PersonDto personElDoradoCounty = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(personElDoradoCounty);
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // when
    final int status = clientTestRule
        .withSecurityToken(AUTHORIZED_MARLIN_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + postedAssessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(postedAssessment, MediaType.APPLICATION_JSON_TYPE)).getStatus();

    // then
    assertThat(status, is(403));

    // clean up
    personHelper.pushToCleanUpPerson(personElDoradoCounty);
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void getAssessment_authorized_whenUserHasSealedAndClientIsSealed() throws IOException {
    // given
    final PersonDto personDto = personHelper.readPersonDto(FIXTURE_POST_PERSON);
    personDto.setSensitivityType(SensitivityType.SEALED);
    final PersonDto person = personHelper.postPerson(personDto, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // when
    final int status = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + postedAssessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get().getStatus();

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
    final PersonDto person = personHelper.postPerson(personDto, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // when
    final int status = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + postedAssessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get().getStatus();

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
    final PersonDto person = personHelper.postPerson(FIXTURE_POST_PERSON, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // when
    final int status = clientTestRule
        .withSecurityToken(AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + postedAssessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get().getStatus();

    // then
    assertThat(status, is(403));

    // clean up
    personHelper.pushToCleanUpPerson(person);
    cleanUpAssessments.push(postedAssessment);
  }

  private AssessmentDto postAssessment(
      AssessmentDto assessment,
      PersonDto person,
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
