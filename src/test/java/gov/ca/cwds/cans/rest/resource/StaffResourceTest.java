package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.AssertFixtureUtils.assertResponseByFixturePath;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import java.io.IOException;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author denys.davydov */
public class StaffResourceTest extends AbstractFunctionalTest {

  private static final String SUBORDINATE_MADERA = "fixtures/perry-account/subordinate-madera.json";
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
  public void getSubordinates_success_whenRecordsExist() throws IOException, JSONException {
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
    final Response actualResponse =
        clientTestRule
            .withSecurityToken(SUPERVISOR_MADERA_ALL_AUTHORIZED)
            .target(API.STAFF + SLASH + API.SUBORDINATES)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

    // then
    assertResponseByFixturePath(
        actualResponse, "fixtures/subordinates-of-supervisor-response.json");
  }

//  @Test
//  public void getSubordinates_success_whenSubordinateExistsButNoAssessments() throws IOException {
//    // when
//    final StaffStatisticsDto[] actual =
//        clientTestRule
//            .withSecurityToken(SUPERVISOR_MADERA_ALL_AUTHORIZED)
//            .target(API.STAFF + SLASH + API.SUBORDINATES)
//            .request(MediaType.APPLICATION_JSON_TYPE)
//            .get()
//            .readEntity(StaffStatisticsDto[].class);
//
//    // then
//    assertThat(actual.length, is(0));
//  }
//
//  @Test
//  public void getSubordinates_success_whenNoSubordinates() throws IOException {
//    // when
//    final StaffStatisticsDto[] actual =
//        clientTestRule
//            .withSecurityToken(SUPERVISOR_MADERA_ALL_AUTHORIZED)
//            .target(API.STAFF + SLASH + API.SUBORDINATES)
//            .request(MediaType.APPLICATION_JSON_TYPE)
//            .get()
//            .readEntity(StaffStatisticsDto[].class);
//
//    // then
//    assertThat(actual.length, is(0));
//  }

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
