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
import java.io.IOException;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author denys.davydov */
public class StaffResourceTest extends AbstractFunctionalTest {

  private static final String SUBORDINATE_MADERA = "fixtures/perry-account/subordinate-madera.json";
  private static final String SUPERVISOR_NO_SUBORDINATES =
      "fixtures/perry-account/supervisor-with-no-subordinates.json";
  private static final String FIXTURES_POST_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  private static final String FIXTURE_POST_SUBMITTED_ASSESSMENT =
      "fixtures/assessment/assessment-post-submit-success.json";
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
  public void getSubordinates_success_whenRecordsExist() throws IOException {
    // given
    final PersonDto person = postPerson();
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    assessment.setPerson(person);
    postAssessment(assessment);
    postAssessment(assessment);
    final AssessmentDto submittedAssessment =
        readObject(FIXTURE_POST_SUBMITTED_ASSESSMENT, AssessmentDto.class);
    submittedAssessment.setPerson(person);
    postAssessment(submittedAssessment);

    // when
    final StaffStatisticsDto[] actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_MADERA_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + API.SUBORDINATES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto[].class);

    // then
    assertThat(actual.length, is(1));
    final StaffStatisticsDto actualStatistics = actual[0];
    assertThat(actualStatistics.getStaffPerson().getIdentifier(), is("aad"));
    assertThat(actualStatistics.getInProgressCount(), is(2L));
    assertThat(actualStatistics.getSubmittedCount(), is(1L));
  }

  @Test
  public void getSubordinates_success_whenSubordinateExistsButNoAssessments() throws IOException {
    // when
    final StaffStatisticsDto[] actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_MADERA_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + API.SUBORDINATES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffStatisticsDto[].class);

    // then
    assertThat(actual.length, is(1));
    final StaffStatisticsDto actualStatistics = actual[0];
    assertThat(actualStatistics.getStaffPerson().getIdentifier(), is("aad"));
    assertThat(actualStatistics.getInProgressCount(), is(0L));
    assertThat(actualStatistics.getSubmittedCount(), is(0L));
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
  public void findAssignedPersonsForStaffId_validStatuses_whenCalled() throws IOException {
    final String staffId = "0Ki";
    final StaffClientDto[] actual =
        clientTestRule
            .withSecurityToken(SUPERVISOR_NO_SUBORDINATES)
            .target(API.STAFF + SLASH + staffId + SLASH + API.PEOPLE)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(StaffClientDto[].class);
    System.out.println(actual);
  }

  private PersonDto postPerson() throws IOException {
    final CountyDto county = (CountyDto) new CountyDto().setName("Madera").setId(20L);
    final PersonDto person = personHelper.readPersonDto(FIXTURES_POST_PERSON).setCounty(county);
    return personHelper.postPerson(person, SUBORDINATE_MADERA);
  }

  private AssessmentDto postAssessment(AssessmentDto assessment) throws IOException {
    AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(SUBORDINATE_MADERA)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    cleanUpAssessments.push(postedAssessment);
    return postedAssessment;
  }
}
