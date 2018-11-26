package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import java.io.IOException;
import java.util.Stack;
import javafx.util.Pair;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

public class SupervisorAuthorizationResourceTest extends AbstractFunctionalTest {

  public static final String FIXTURES_CLIENT_ASSIGNED_TO_0HS =
      "fixtures/supervisor/client-of-0HS-rw-assignment.json";
  public static final String FIXTURES_CLIENT_ASSIGNED_TO_00P =
      "fixtures/supervisor/client-of-00p-rw-assignment.json";
  public static final String FIXTURES_SUPERVISOR_06S_SANTA_CLARA =
      "fixtures/supervisor/06s-santa-clara-supervisor.json";
  public static final String FIXTURES_SUPERVISOR_00j_MERCED =
      "fixtures/supervisor/00j-merced-supervisor.json";
  private final Stack<Pair<Long, String>> cleanUpAssessmentsToUserFixtures = new Stack<>();

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessmentsToUserFixtures.empty()) {
      final Pair<Long, String> assessmentIdToUserFixture = cleanUpAssessmentsToUserFixtures.pop();
      clientTestRule
          .withSecurityToken(assessmentIdToUserFixture.getValue())
          .target(ASSESSMENTS + SLASH + assessmentIdToUserFixture.getKey())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Test
  public void getClient_success_whenAssignedToSubordinateThroughCase() throws IOException {
    getClient("DTdjk8J0HS", FIXTURES_SUPERVISOR_06S_SANTA_CLARA, HttpStatus.SC_OK);
  }

  @Test
  public void getClient_success_whenAssignedToSubordinateThroughReferral() throws IOException {
    getClient("2Tao9dx00j", FIXTURES_SUPERVISOR_00j_MERCED, HttpStatus.SC_OK);
  }

  @Test
  public void getClient_forbidden_whenNotAssignedToSubordinateAndFromOtherCounty()
      throws IOException {
    getClient("AgJLkWe0Ki", FIXTURES_SUPERVISOR_06S_SANTA_CLARA, HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void postAssessment_success_whenSubordinateHasAssignmentThroughCase() throws Exception {
    postAssessmentAndCheckStatus(
        FIXTURES_CLIENT_ASSIGNED_TO_0HS,
        FIXTURES_SUPERVISOR_06S_SANTA_CLARA,
        HttpStatus.SC_CREATED);
  }

  @Test
  public void postAssessment_success_whenSubordinateHasAssignmentThroughReferral()
      throws Exception {
    postAssessmentAndCheckStatus(
        FIXTURES_CLIENT_ASSIGNED_TO_00P, FIXTURES_SUPERVISOR_00j_MERCED, HttpStatus.SC_CREATED);
  }

  @Test
  public void postAssessment_forbidden_whenSupervisorFromOtherCounty() throws Exception {
    postAssessmentAndCheckStatus(
        FIXTURES_CLIENT_ASSIGNED_TO_00P,
        FIXTURES_SUPERVISOR_06S_SANTA_CLARA,
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void postAssessment_forbidden_whenNotAssigned() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-sealed.json",
        FIXTURES_SUPERVISOR_06S_SANTA_CLARA,
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void getAssessment_success_whenSupervisorHasSubordinatesAssigned() throws Exception {
    AssessmentDto assessment = createAssessmentDto(FIXTURES_CLIENT_ASSIGNED_TO_0HS);
    assessment =
        postAssessmentAndGetResponse(assessment, FIXTURES_SUPERVISOR_06S_SANTA_CLARA)
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(), FIXTURES_SUPERVISOR_06S_SANTA_CLARA, HttpStatus.SC_OK);
    pushToCleanUpStack(assessment.getId(), FIXTURES_SUPERVISOR_06S_SANTA_CLARA);
  }

  @Test
  public void getAssessment_forbidden_whenSupervisorHasNoSubordinatesAssigned() throws Exception {
    AssessmentDto assessment = createAssessmentDto(FIXTURES_CLIENT_ASSIGNED_TO_0HS);
    assessment =
        postAssessmentAndGetResponse(assessment, FIXTURES_SUPERVISOR_06S_SANTA_CLARA)
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(), FIXTURES_SUPERVISOR_00j_MERCED, HttpStatus.SC_FORBIDDEN);
    pushToCleanUpStack(assessment.getId(), FIXTURES_SUPERVISOR_06S_SANTA_CLARA);
  }
}
