package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.STAFF;
import static gov.ca.cwds.cans.domain.enumeration.AssessmentStatus.COMPLETED;
import static gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus.IN_PROGRESS;
import static gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus.NO_PRIOR_CANS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/** @author denys.davydov */
public class StaffResourceTest extends AbstractFunctionalTest {

  private static final String SUBORDINATE_MADERA =
      "fixtures/perry-account/subordinate-san-louis.json";
  private static final String SUPERVISOR_NO_SUBORDINATES =
      "fixtures/perry-account/supervisor-with-no-subordinates.json";
  private static final String FIXTURES_POST_RW_PERSON = "fixtures/client-of-0Ki-rw-assignment.json";
  private static final String FIXTURES_POST_R_PERSON = "fixtures/client-of-0Ki-r-assignment.json";
  private static final String FIXTURE_ASSIGNED_CASEWORKER =
      "fixtures/perry-account/0ki-napa-all.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_COMPLETED_ASSESSMENT =
      "fixtures/assessment/assessment-post-complete-success.json";
  private static final String ASSIGNED_STAFF_ID = "0ME";
  private static final String PERSON_ID_0 = "AfhccGA0Co";
  private static final String PERSON_ID_1 = "Ar9aZQx0En";

  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();
  private final String TEST_EXTERNAL_ID = "Ar9aZQx0En";
  private final String TEST_STAFF_ID = "0ME";

  private String tearDownToken = SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED;

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(tearDownToken)
          .target(ASSESSMENTS + SLASH + assessmentToDelete.getId())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    this.tearDownToken = SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED;
  }

  @Test
  public void getSubordinates_success_whenRecordsExist() throws IOException {
    // given
    postTestAssessmentsForAssignedStaffId();

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

  private void postTestAssessmentsForAssignedStaffId() throws IOException {
    final AssessmentDto person0Assessment =
        postAssessmentWithPerson(PERSON_ID_0, FIXTURE_POST_ASSESSMENT);
    postAssessment(person0Assessment); // the same date assessment

    final AssessmentDto person1Assessment =
        postAssessmentWithPerson(PERSON_ID_1, FIXTURE_POST_COMPLETED_ASSESSMENT);
    person1Assessment.setEventDate(person1Assessment.getEventDate().plusDays(10));
    postAssessment(person1Assessment);
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

  private AssessmentDto postAssessmentWithPerson(
      final String personIdentifier, final String assessmentFixture) throws IOException {
    final AssessmentDto assessment = readObject(assessmentFixture, AssessmentDto.class);
    final ClientDto client = (ClientDto) new ClientDto().setIdentifier(personIdentifier);
    assessment.setPerson(client);
    postAssessment(assessment);
    return assessment;
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

  private AssessmentDto postAssessmentForGetAll(
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
  public void getStaffPersonWithStatistics_404_whenNoStaffPersonFound() throws IOException {
    // when
    final int actualStatus =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + "NOT")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(actualStatus, is(404));
  }

  @Test
  public void getStaffPersonWithStatistics_403_whenNotAuthorized() throws IOException {
    // when
    final int actualStatus =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + "aa1")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(actualStatus, is(403));
  }

  @Test
  public void getStaffPersonWithStatistics_422_whenInvalidStaffId() throws IOException {
    // when
    final int actualStatus =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + "InvalidStaffId")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(actualStatus, is(422));
  }

  @Test
  public void getStaffPersonWithStatistics_success_whenEmptyStatistics() throws IOException {
    // when
    final StaffStatisticsDto actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + ASSIGNED_STAFF_ID)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto.class);

    // then
    assertThat(actual.getStaffPerson().getIdentifier(), is(ASSIGNED_STAFF_ID));
    assertStatistics(actual, 0, 0);
  }

  @Test
  public void getStaffPersonWithStatistics_success_whenStatisticsExists() throws IOException {
    // given
    postTestAssessmentsForAssignedStaffId();

    // when
    final StaffStatisticsDto actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + ASSIGNED_STAFF_ID)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto.class);

    // then
    assertThat(actual.getStaffPerson().getIdentifier(), is(ASSIGNED_STAFF_ID));
    assertThat(actual.getStaffPerson().getCounty().getName(), is(notNullValue()));
    assertStatistics(actual, 1, 1);
  }

  @Test
  public void findPersonsByStaffId_403_whenNotAuthorized() throws IOException {
    // when
    final int actualStatus =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + "aa1" + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(actualStatus, is(403));
  }

  @Test
  public void findPersonsByStaffId_422_whenInvalidStaffId() throws IOException {
    // when
    final int actualStatus =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + "InvalidStaffId" + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .getStatus();

    // then
    assertThat(actualStatus, is(422));
  }

  @Test
  public void findPersonsByStaffId_statusIsNO_PRIOR_CANS_whenNoPriorCans() throws IOException {
    final StaffClientDto[] response =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + TEST_STAFF_ID + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffClientDto[].class);
    List<StaffClientDto> dtoList = Arrays.asList(response);
    Assert.assertTrue(
        dtoList
            .stream()
            .allMatch(
                item -> item.getStatus().equals(NO_PRIOR_CANS) && item.getReminderDate() == null));
    List<StaffClientDto> subList =
        dtoList
            .stream()
            .filter(item -> item.getIdentifier().equals(TEST_EXTERNAL_ID))
            .collect(Collectors.toList());

    Assert.assertEquals(1, subList.size());
    StaffClientDto staffClientDto = subList.get(0);
    validateCommonFields(staffClientDto);
  }

  @Test
  public void findPersonsByStaffId_statusIsLastAssessmentStatus_whenMultipleAssessements()
      throws IOException {
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    ClientDto clientDto = new ClientDto();
    clientDto.setIdentifier(TEST_EXTERNAL_ID);
    assessment.setPerson(clientDto);
    assessment.setEventDate(LocalDate.now());
    assessment.setStatus(AssessmentStatus.IN_PROGRESS);
    postAssessment(assessment);
    assessment.setEventDate(LocalDate.now());
    assessment.setStatus(COMPLETED);
    postAssessment(assessment);
    final StaffClientDto[] response =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + TEST_STAFF_ID + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffClientDto[].class);
    List<StaffClientDto> dtoList = Arrays.asList(response);
    List<StaffClientDto> completed =
        dtoList
            .stream()
            .filter(item -> item.getStatus().equals(IN_PROGRESS))
            .collect(Collectors.toList());
    Assert.assertEquals(1, completed.size());
    StaffClientDto staffClientDto = completed.get(0);
    validateCommonFields(staffClientDto);
    Assert.assertEquals(staffClientDto.getReminderDate(), LocalDate.now().plusMonths(6));
  }

  @Test
  public void getAllAssessments_findsFiveRecords() throws IOException {
    // given
    final List<Long> assessmentIds = new ArrayList<>();
    final ClientDto person = readObject(FIXTURES_POST_RW_PERSON, ClientDto.class);
    final ClientDto otherPerson = readObject(FIXTURES_POST_R_PERSON, ClientDto.class);
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    final List<Object[]> properties =
        Arrays.asList(
            new Object[] {
              person,
              AssessmentStatus.IN_PROGRESS,
              LocalDate.of(2010, 1, 1),
              FIXTURE_ASSIGNED_CASEWORKER
            },
            new Object[] {
              person,
              AssessmentStatus.IN_PROGRESS,
              LocalDate.of(2015, 10, 10),
              FIXTURE_ASSIGNED_CASEWORKER
            },
            // out of search results because of the other person
            new Object[] {
              otherPerson,
              AssessmentStatus.IN_PROGRESS,
              LocalDate.of(2015, 10, 10),
              FIXTURE_ASSIGNED_CASEWORKER
            },
            new Object[] {person, COMPLETED, LocalDate.of(2010, 1, 1), FIXTURE_ASSIGNED_CASEWORKER},
            new Object[] {
              person, COMPLETED, LocalDate.of(2015, 10, 10), FIXTURE_ASSIGNED_CASEWORKER
            },
            // out of search results because of the other created by user
            new Object[] {
              person, COMPLETED, LocalDate.of(2015, 10, 10), NOT_AUTHORIZED_ACCOUNT_FIXTURE
            });

    for (Object[] property : properties) {
      final AssessmentDto newAssessment =
          postAssessmentForGetAll(
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
    final AssessmentMetaDto[] actualResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .target(STAFF + SLASH + ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(AssessmentMetaDto[].class);

    // then
    assertThat(actualResults.length, is(5));
    assertThat(actualResults[0].getId(), is(assessmentIds.get(1)));
    assertThat(actualResults[1].getId(), is(assessmentIds.get(2)));
    assertThat(actualResults[2].getId(), is(assessmentIds.get(0)));
    assertThat(actualResults[3].getId(), is(assessmentIds.get(4)));
    assertThat(actualResults[4].getId(), is(assessmentIds.get(3)));

    // clean preparation
    this.tearDownToken = AUTHORIZED_NAPA_ACCOUNT_FIXTURE;
  }

  private void validateCommonFields(StaffClientDto staffClientDto) {
    Assert.assertEquals(staffClientDto.getFirstName(), "TeenageBoy");
    Assert.assertEquals(staffClientDto.getDob(), LocalDate.parse("1982-09-08"));
    Assert.assertEquals(staffClientDto.getIdentifier(), TEST_EXTERNAL_ID);
  }
}
