package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.ClientAssessmentStatus;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import java.io.IOException;
import java.time.LocalDate;
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

/**
 * @author denys.davydov
 */
public class StaffResourceTest extends AbstractFunctionalTest {

  private static final String SUBORDINATE_MADERA =
      "fixtures/perry-account/subordinate-san-louis.json";
  private static final String SUPERVISOR_NO_SUBORDINATES =
      "fixtures/perry-account/supervisor-with-no-subordinates.json";
  private static final String FIXTURES_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_COMPLETED_ASSESSMENT =
      "fixtures/assessment/assessment-post-complete-success.json";
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
    final PersonDto person = postPerson();
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    assessment.setPerson(person);
    postAssessment(assessment);
    postAssessment(assessment);
    final AssessmentDto completedAssessment =
        readObject(FIXTURE_POST_COMPLETED_ASSESSMENT, AssessmentDto.class);
    completedAssessment.setPerson(person);
    postAssessment(completedAssessment);

    // when
    final StaffStatisticsDto[] actualDtos =
        clientTestRule
            .withSecurityToken(SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + API.SUBORDINATES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto[].class);

    // then
    final List<Object[]> actual =
        Arrays.stream(actualDtos).map(this::toObjectArray).collect(Collectors.toList());

    assertThat(
        actual,
        containsInAnyOrder(
            toObjectArray("0Ht", 2, 1, 52),
            toObjectArray("0ME", 0, 0, 23),
            toObjectArray("0I2", 0, 0, 0)));
  }

  private Object[] toObjectArray(final StaffStatisticsDto statistics) {
    final Object[] result = new Object[4];
    result[0] = statistics.getStaffPerson().getIdentifier();
    result[1] = statistics.getInProgressCount();
    result[2] = statistics.getCompletedCount();
    result[3] = statistics.getClientsCount();
    return result;
  }

  private Object[] toObjectArray(
      String id, int inProgressCount, int submittedCount, int clientsCount) {
    return new Object[]{id, inProgressCount, submittedCount, clientsCount};
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
    Assert.assertEquals(staffClientDto.getFirstName(),"child");
    Assert.assertEquals(staffClientDto.getLastName(),"Hoofe");
    Assert.assertEquals(staffClientDto.getDob(), LocalDate.parse("2000-11-23"));
    Assert.assertEquals(staffClientDto.getId(),person.getId());
    Assert.assertEquals(staffClientDto.getExternalId(), TEST_EXTERNAL_ID);
  }

  private PersonDto postPerson() throws IOException {
    return postPerson(null);
  }

  private PersonDto postPerson(String externalId) throws IOException {
    final CountyDto county = (CountyDto) new CountyDto().setName("San Luis Obispo").setId(40L);
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST_PERSON).setCounty(county);
    if (externalId != null) {
      person.setExternalId(externalId);
    }
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
}
