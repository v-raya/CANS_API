package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** @author denys.davydov */
public class StaffResourceTest extends AbstractFunctionalTest {

  private static final String SUBORDINATE_MADERA =
      "fixtures/perry-account/subordinate-san-louis.json";
  private static final String SUPERVISOR_NO_SUBORDINATES =
      "fixtures/perry-account/supervisor-with-no-subordinates.json";
  private static final String FIXTURES_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_COMPLETED_ASSESSMENT =
      "fixtures/assessment/assessment-post-complete-success.json";
  private static final String BASE10_ID_0 = "1465-4794-4022-7001119"; // -> "PndSNox0I3"
  private static final String BASE10_ID_1 = "0150-1373-1721-9001119"; // -> "2dsesiZ0I3"
  private static final String SAN_LUIS_OBISPO_NAME = "San Luis Obispo";
  private static final long SAN_LUIS_OBISPO_ID = 40L;

  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();
  private final String TEST_EXTERNAL_ID = "92PghIc0Ki";
  private final String TEST_STAFF_ID = "0Ki";
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
  public void getSubordinates_success_whenRecordsExist() throws IOException {
    // given
    final AssessmentDto person0Assessment =
        postPersonWithAssessment(BASE10_ID_0, FIXTURE_POST_ASSESSMENT);
    postAssessment(person0Assessment); // the same date assessment

    final AssessmentDto person1Assessment =
        postPersonWithAssessment(BASE10_ID_1, FIXTURE_POST_COMPLETED_ASSESSMENT);
    person1Assessment.setEventDate(person1Assessment.getEventDate().plusDays(10));
    postAssessment(person1Assessment);

    // when
    final StaffStatisticsDto[] actualDtos =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + API.SUBORDINATES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto[].class);

    // then
    final StaffStatisticsDto staffOne = findStatisticsByStaffId(actualDtos, "0ME");
    assertStatistics(staffOne, 1, 1);

    final StaffStatisticsDto staffTwo = findStatisticsByStaffId(actualDtos, "0I2");
    assertStatistics(staffTwo, 0, 0);
  }

  private StaffStatisticsDto findStatisticsByStaffId(
      final StaffStatisticsDto[] actualDtos, final String staffId) {
    return Arrays.stream(actualDtos)
        .filter(stat -> staffId.equals(stat.getStaffPerson().getIdentifier()))
        .findFirst()
        .get();
  }

  private void assertStatistics(
      final StaffStatisticsDto stat, final int inProgressCount, final int completedCount) {
    final String reason = "Statistics: " + stat.toString();
    assertThat(reason, stat.getInProgressCount(), is(inProgressCount));
    assertThat(reason, stat.getCompletedCount(), is(completedCount));
    assertThat(
        reason,
        stat.getClientsCount(),
        is(stat.getNoPriorCansCount() + stat.getInProgressCount() + stat.getCompletedCount()));
  }

  private AssessmentDto postPersonWithAssessment(
      final String personExternalId, final String assessmentFixture) throws IOException {
    final PersonDto person = postPerson(personExternalId);
    final AssessmentDto assessment = readObject(assessmentFixture, AssessmentDto.class);
    assessment.setPerson(person);
    postAssessment(assessment);
    return assessment;
  }

  private PersonDto postPerson(final String externalId) throws IOException {
    final CountyDto county =
        (CountyDto) new CountyDto().setName(SAN_LUIS_OBISPO_NAME).setId(SAN_LUIS_OBISPO_ID);
    final PersonDto person =
        (PersonDto)
            personHelper
                .readPersonDto(FIXTURES_POST_PERSON)
                .setCounty(county)
                .setExternalId(externalId);
    return personHelper.postPerson(person, SUBORDINATE_MADERA);
  }

  private void postAssessment(AssessmentDto assessment) throws IOException {
    AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(SUBORDINATE_MADERA)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    cleanUpAssessments.push(postedAssessment);
  }

  @Test
  public void getSubordinates_empty_whenNoSubordinates() throws IOException {
    // when
    final StaffStatisticsDto[] actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + API.SUBORDINATES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto[].class);

    // then
    assertThat(actual.length, is(0));
  }

  @Test
  public void findAssignedPersonsForStaffId_statusIsNO_PRIOR_CANS_whenNoPriorCans()
      throws IOException {

    PersonDto personDto = postPerson(TEST_EXTERNAL_ID);
    final StaffClientDto[] actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + TEST_STAFF_ID + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffClientDto[].class);
    Assert.assertTrue(actual.length == 1);
    StaffClientDto staffClientDto = actual[0];
    validateCommonFields(staffClientDto, personDto);
    Assert.assertTrue(staffClientDto.getStatus().equals(ClientAssessmentStatus.NO_PRIOR_CANS));
    Assert.assertNull(staffClientDto.getReminderDate());
  }

  @Test
  public void findAssignedPersonsForStaffId_statusIsLastAssessmentStatus_whenMultipleAssessements()
      throws IOException {

    PersonDto person = postPerson(TEST_EXTERNAL_ID);
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    assessment.setPerson(person);
    assessment.setEventDate(LocalDate.now().minusYears(1));
    assessment.setStatus(AssessmentStatus.IN_PROGRESS);
    postAssessment(assessment);
    assessment.setEventDate(LocalDate.now().minusMonths(6));
    assessment.setStatus(AssessmentStatus.COMPLETED);
    postAssessment(assessment);
    final StaffClientDto[] actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + TEST_STAFF_ID + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffClientDto[].class);
    Assert.assertTrue(actual.length == 1);
    StaffClientDto staffClientDto = actual[0];
    validateCommonFields(staffClientDto, person);
    Assert.assertEquals(staffClientDto.getStatus(), ClientAssessmentStatus.COMPLETED);
    Assert.assertEquals(staffClientDto.getReminderDate(), LocalDate.now());
  }

  private void validateCommonFields(StaffClientDto staffClientDto, PersonDto person) {
    Assert.assertEquals(staffClientDto.getFirstName(), "child");
    Assert.assertEquals(staffClientDto.getLastName(), "Hoofe");
    Assert.assertEquals(staffClientDto.getDob(), LocalDate.parse("2000-11-23"));
    Assert.assertEquals(staffClientDto.getId(), person.getId());
    Assert.assertEquals(staffClientDto.getExternalId(), TEST_EXTERNAL_ID);
  }
}
