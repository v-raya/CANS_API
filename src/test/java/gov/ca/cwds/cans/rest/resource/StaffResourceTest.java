package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.STAFF;
import static gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus.COMPLETED;
import static gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus.NO_PRIOR_CANS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
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
  private static final String PERSON_ID_0 = "PndSNox0I3";
  private static final String PERSON_ID_0_BASE10 = "1465-4794-4022-7001119";
  private static final String PERSON_ID_1 = "2dsesiZ0I3";
  private static final String PERSON_ID_1_BASE10 = "0150-1373-1721-9001119";
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
        postAssessmentWithPerson(PERSON_ID_0, PERSON_ID_0_BASE10, FIXTURE_POST_ASSESSMENT);
    postAssessment(person0Assessment); // the same date assessment

    final AssessmentDto person1Assessment =
        postAssessmentWithPerson(
            PERSON_ID_1, PERSON_ID_1_BASE10, FIXTURE_POST_COMPLETED_ASSESSMENT);
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

  private AssessmentDto postAssessmentWithPerson(
      final String personIdentifier, final String personExternalId, final String assessmentFixture)
      throws IOException {
    final AssessmentDto assessment = readObject(assessmentFixture, AssessmentDto.class);
    final ClientDto client =
        (ClientDto) new ClientDto().setIdentifier(personIdentifier).setExternalId(personExternalId);
    assessment.setPerson(client);
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
                .setExternalId(externalId)
                .setIdentifier(externalId);
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
  public void findAssignedPersonsForStaffId_statusIsNO_PRIOR_CANS_whenNoPriorCans()
      throws IOException {

    postPerson(TEST_EXTERNAL_ID);
    final StaffClientDto[] response =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
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
  public void findAssignedPersonsForStaffId_statusIsLastAssessmentStatus_whenMultipleAssessements()
      throws IOException {

    PersonDto person = postPerson(TEST_EXTERNAL_ID);
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    ClientDto clientDto = new ClientDto();
    clientDto.setIdentifier(TEST_EXTERNAL_ID);
    assessment.setPerson(clientDto);
    assessment.setEventDate(LocalDate.now());
    assessment.setStatus(AssessmentStatus.IN_PROGRESS);
    postAssessment(assessment);
    assessment.setEventDate(LocalDate.now());
    assessment.setStatus(AssessmentStatus.COMPLETED);
    postAssessment(assessment);
    final StaffClientDto[] response =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + TEST_STAFF_ID + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffClientDto[].class);
    List<StaffClientDto> dtoList = Arrays.asList(response);
    List<StaffClientDto> completed =
        dtoList
            .stream()
            .filter(item -> item.getStatus().equals(COMPLETED))
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
    final ClientDto person = readObject(FIXTURES_POST_PERSON, ClientDto.class);
    final ClientDto otherPerson = readObject(FIXTURES_POST_PERSON, ClientDto.class);
    otherPerson.setIdentifier("aaaaaaaaaa");
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    final List<Object[]> properties =
        Arrays.asList(
            new Object[] {
                person,
                AssessmentStatus.IN_PROGRESS,
                LocalDate.of(2010, 1, 1),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
                person,
                AssessmentStatus.IN_PROGRESS,
                LocalDate.of(2015, 10, 10),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            // out of search results because of the other person
            new Object[] {
                otherPerson,
                AssessmentStatus.IN_PROGRESS,
                LocalDate.of(2015, 10, 10),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
                person,
                AssessmentStatus.COMPLETED,
                LocalDate.of(2010, 1, 1),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            },
            new Object[] {
                person,
                AssessmentStatus.COMPLETED,
                LocalDate.of(2015, 10, 10),
                AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE
            }
            /*, Authorization going to be reworked
            // out of search results because of the other created by user
            new Object[] {
              person, COMPLETED, LocalDate.of(2015, 10, 10), NOT_AUTHORIZED_ACCOUNT_FIXTURE
            }*/
        );

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
        personHelper.pushToCleanUpPerson(newAssessment.getPerson());
      }
    }
    // when
    final AssessmentMetaDto[] actualResults =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
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
  }

  private void validateCommonFields(StaffClientDto staffClientDto) {
    Assert.assertEquals(staffClientDto.getFirstName(), "child");
    Assert.assertEquals(staffClientDto.getLastName(), "Hoofe");
    Assert.assertEquals(staffClientDto.getDob(), LocalDate.parse("2000-11-23"));
    Assert.assertEquals(staffClientDto.getIdentifier(), TEST_EXTERNAL_ID);
  }
}
