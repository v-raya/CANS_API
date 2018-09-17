package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.INSTRUMENTS;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.Constants.API.START;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.IN_PROGRESS;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.SUBMITTED;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static gov.ca.cwds.cans.test.util.FixtureReader.readRestObject;
import static gov.ca.cwds.cans.util.DtoCleaner.cleanDtoIfNeed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.test.util.FixtureReader;
import gov.ca.cwds.rest.exception.BaseExceptionResponse;
import gov.ca.cwds.rest.exception.IssueDetails;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

/**
 * @author denys.davydov
 */
public class AssessmentResourceTest extends AbstractFunctionalTest {

  private static final String AUTHORIZED_MARLIN_ACCOUNT_FIXTURE =
      "fixtures/perry-account/marlin-all-authorized.json";
  private static final String FIXTURE_POST_INSTRUMENT = "fixtures/instrument-post.json";
  private static final String FIXTURE_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_SUBMIT_INVALID = "fixtures/assessment/assessment-post-submit-fail.json";
  private static final String FIXTURE_POST_LOGGING_INFO =
      "fixtures/assessment/assessment-post-logging-info.json";
  private static final String FIXTURE_READ = "fixtures/assessment/assessment-read.json";
  private static final String FIXTURE_EMPTY_OBJECT = "fixtures/empty-object.json";
  private final Set<Long> cleanUpAssessmentIds = new HashSet<>();
  private final Set<Long> cleanUpPeopleIds = new HashSet<>();
  private Long cleanUpInstrumentId;

  @After
  public void tearDown() throws IOException {
    for (Long assessmentId : cleanUpAssessmentIds) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(ASSESSMENTS + SLASH + assessmentId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    if (cleanUpInstrumentId != null) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(INSTRUMENTS + SLASH + cleanUpInstrumentId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    for (Long personId : cleanUpPeopleIds) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(PEOPLE + SLASH + personId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Test
  public void startDemoAssessment_success() throws IOException {
    // given
    final PersonDto person = postPerson();
    final StartAssessmentRequest request = readObject(FIXTURE_START, StartAssessmentRequest.class);
    request.setInstrumentId(1L);
    request.setPersonId(person.getId());

    // when
    final Response postResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS + SLASH + START)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
    final AssessmentDto assessment = postResponse.readEntity(AssessmentDto.class);

    // then
    assertThat(postResponse.getStatus(), is(200));
    assertThat(assessment, is(not(nullValue())));
    assertThat(assessment.getCounty().getId(), is(9L));

    // clean up
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(assessment.getId());
  }

  @Test
  public void startAssessment_success() throws IOException {
    // given
    final Entity newInstrument = readRestObject(FIXTURE_POST_INSTRUMENT, InstrumentDto.class);
    cleanUpInstrumentId =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(INSTRUMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(newInstrument)
            .readEntity(InstrumentDto.class)
            .getId();
    final PersonDto person = postPerson();
    final StartAssessmentRequest startRequest =
        readObject(FIXTURE_START, StartAssessmentRequest.class);
    startRequest.setInstrumentId(cleanUpInstrumentId);
    startRequest.setPersonId(person.getId());

    // when
    final AssessmentDto actual = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + START)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(startRequest, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);

    // then
    actual.setId(null);
    cleanDtoIfNeed(actual);
    final AssessmentDto expected = FixtureReader.readObject(FIXTURE_READ, AssessmentDto.class);
    expected.setInstrumentId(cleanUpInstrumentId);
    expected.setPerson(person);
    expected.setCounty(person.getCounty());
    assertThat(actual, is(expected));

    // clean up
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(actual.getId());
    cleanUpAssessmentIds.add(actual.getId());
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
    final PersonDto person = postPerson();
    final AssessmentDto inputAssessment =
        readObject(FIXTURE_POST_LOGGING_INFO, AssessmentDto.class);
    inputAssessment.setPerson(person);

    // when
    final AssessmentDto actualAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
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
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(actualAssessment.getId());
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
    final PersonDto person = postPerson();
    final PersonDto otherPerson = postPerson();

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
    cleanUpAssessmentIds.addAll(assessmentIds);
    this.cleanUpPeopleIds.add(person.getId());
    this.cleanUpPeopleIds.add(otherPerson.getId());
  }

  @Test
  public void putAssessment_assessmentCaseNumberUpdated_whenPersonsCaseNumberUpdated()
      throws IOException {
    // given
    final PersonDto person0 = readObject(FIXTURE_POST_PERSON, PersonDto.class);
    person0.getCases().get(0).setExternalId("4321-321-4321-87654321");
    final PersonDto postedPerson0 = clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(person0, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(PersonDto.class);

    final PersonDto person = postPerson();
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
    cleanUpPeopleIds.add(person.getId());
    cleanUpPeopleIds.add(postedPerson0.getId());
    cleanUpAssessmentIds.add(postedAssessment.getId());
  }

  @Test
  public void putAssessment_notUpdatingCounty_whenUpdatingAssessment() throws IOException {
    // given
    final PersonDto person = postPerson();
    final AssessmentDto assessment = readObject(FIXTURE_POST, AssessmentDto.class);
    assessment.setPerson(person);
    final AssessmentDto postedAssessment = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
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
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(postedAssessment.getId());
  }

  @Test
  public void putAssessment_unauthorized_whenUserFromDifferentCounty() throws IOException {
    // given
    final PersonDto personElDoradoCounty = postPerson();
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
    cleanUpPeopleIds.add(personElDoradoCounty.getId());
    cleanUpAssessmentIds.add(postedAssessment.getId());
  }

  @Test
  public void getAssessment_authorized_whenUserHasSealedAndClientIsSealed() throws IOException {
    // given
    final Entity<PersonDto> personEntity = readRestObject(FIXTURE_POST_PERSON, PersonDto.class);
    personEntity.getEntity().setSensitivityType(SensitivityType.SEALED);
    final PersonDto person = postPerson(personEntity);
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
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(postedAssessment.getId());
  }

  @Test
  public void getAssessment_unauthorized_whenUserHasSealedAndClientIsSealedButDifferentCounty()
      throws IOException {
    // given
    final Entity<PersonDto> personEntity = readRestObject(FIXTURE_POST_PERSON, PersonDto.class);
    personEntity.getEntity().setSensitivityType(SensitivityType.SEALED);
    final PersonDto person = postPerson(personEntity);
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
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(postedAssessment.getId());
  }

  @Test
  public void getAssessment_unauthorized_whenUserHasNotSealedAndClientIsSealed()
      throws IOException {
    // given
    final Entity<PersonDto> personEntity = readRestObject(FIXTURE_POST_PERSON,
        PersonDto.class);
    personEntity.getEntity().setSensitivityType(SensitivityType.SEALED);
    final PersonDto person = postPerson(personEntity);
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
    cleanUpPeopleIds.add(person.getId());
    cleanUpAssessmentIds.add(postedAssessment.getId());
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

  private PersonDto postPerson() throws IOException {
    final Entity person = readRestObject(FIXTURE_POST_PERSON, PersonDto.class);
    return postPerson(person);
  }

  private PersonDto postPerson(Entity person) throws IOException {
    return clientTestRule
        .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(person)
        .readEntity(PersonDto.class);
  }
}
