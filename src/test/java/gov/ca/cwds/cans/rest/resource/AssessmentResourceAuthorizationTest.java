package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import java.io.IOException;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class AssessmentResourceAuthorizationTest extends AbstractFunctionalTest {

  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(
              "fixtures/perry-account/0ki-napa-all.json")
          .target(ASSESSMENTS + SLASH + assessmentToDelete.getId())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Test
  public void postAssessment_success_whenUserHasAssignment() throws Exception {
    postAssessmentAndCheckStatus("fixtures/client-of-0Ki-rw-assignment.json",
        "fixtures/perry-account/0ki-marlin-none.json", HttpStatus.SC_CREATED);
  }

  @Test
  public void postAssessment_forbidden_whenUserHasReadOnlyAssignment() throws Exception {
    postAssessmentAndCheckStatus("fixtures/client-of-0Ki-r-assignment.json",
        "fixtures/perry-account/0ki-marlin-none.json", HttpStatus.SC_FORBIDDEN);

  }

  @Test
  public void getAssessment_success_whenUserHasReadOnlyAssignment() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-r-assignment.json");
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/perry-account/0ki-napa-all.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(), "fixtures/perry-account/0ki-marlin-none.json",
        HttpStatus.SC_OK);
  }

  @Test
  public void postAssessment_success_noAssignmentSealedSameCounty() throws Exception {
    postAssessmentAndCheckStatus("fixtures/client-of-0Ki-sealed.json",
        "fixtures/perry-account/account-napa-all.json", HttpStatus.SC_CREATED);
  }

  @Test
  public void postAssessment_success_noAssignmentSensitiveSameCounty() throws Exception {
    postAssessmentAndCheckStatus("fixtures/client-of-0Ki-sensitive.json",
        "fixtures/perry-account/account-napa-all.json", HttpStatus.SC_CREATED);
  }

  @Test
  public void getAssessment_success_noAssignmentSealedSameCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sealed.json");
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/perry-account/0ki-napa-all.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(), "fixtures/perry-account/account-napa-all.json",
        HttpStatus.SC_OK);
  }

  @Test
  public void getAssessment_success_noAssignmentSensitiveSameCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sensitive.json");
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/perry-account/0ki-napa-all.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(), "fixtures/perry-account/account-napa-all.json",
        HttpStatus.SC_OK);
  }

  @Test
  public void postAssessment_failed_noAssignmentSealedDiffCounty() throws Exception {
    postAssessmentAndCheckStatus("fixtures/client-of-0Ki-sealed.json",
        "fixtures/perry-account/el-dorado-all-authorized.json", HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void postAssessment_failed_noAssignmentSensitiveDiffCounty() throws Exception {
    postAssessmentAndCheckStatus("fixtures/client-of-0Ki-sensitive.json",
        "fixtures/perry-account/el-dorado-all-authorized.json", HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void getAssessment_failed_noAssignmentSealedDiffCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sealed.json");
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/perry-account/0ki-napa-all.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(),
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void getAssessment_failed_noAssignmentSensitiveDiffCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sensitive.json");
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/perry-account/0ki-napa-all.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(),
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void getAssessment_failed_noAssignmentDiffCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sensitive.json");
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/perry-account/0ki-napa-all.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(),
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
  }

  private void getAssessmentAndCheckStatus(Long id, String userFixture, int expectedStatus)
      throws IOException {
    int actualStatus = getAssessment(userFixture, id).getStatus();
    Assert.assertEquals(expectedStatus, actualStatus);
  }

  private AssessmentDto createAssessmentDto(String personFixture) throws Exception {
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    final ClientDto person = readObject(personFixture, ClientDto.class);
    assessment.setPerson(person);
    return assessment;
  }

  private void postAssessmentAndCheckStatus(String personFixture, String userFixture,
      int expectedStatus)
      throws Exception {
    AssessmentDto assessment = createAssessmentDto(personFixture);
    Response response = postAssessmentAndGetResponse(assessment, userFixture);
    checkStatus(response, expectedStatus);
  }

  private void checkStatus(Response response, int expectedStatus) {
    int actualStatus = response.getStatus();
    if (actualStatus < 300) {
      AssessmentDto postedAssessment = response.readEntity(AssessmentDto.class);
      cleanUpAssessments.push(postedAssessment);
    }
    Assert.assertEquals(expectedStatus, actualStatus);
  }

  private Response postAssessmentAndGetResponse(AssessmentDto assessment, String userFixture)
      throws IOException {
    return clientTestRule
        .withSecurityToken(userFixture)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE));
  }

  private Response getAssessment(String accountFixture, Long id) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(ASSESSMENTS + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();
  }
}
