package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import java.io.IOException;
import javafx.util.Pair;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

public class AssessmentResourceAuthorizationTest extends AbstractFunctionalTest {

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
  public void postAssessment_success_whenUserHasAssignment() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-rw-assignment.json",
        "fixtures/perry-account/0ki-marlin-none.json",
        HttpStatus.SC_CREATED);
  }

  @Test
  public void postAssessment_forbidden_whenUserHasReadOnlyAssignment() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-r-assignment.json",
        "fixtures/perry-account/0ki-marlin-none.json",
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void getAssessment_success_whenUserHasReadOnlyAssignment() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-r-assignment.json");
    assessment =
        postAssessmentAndGetResponse(assessment, "fixtures/perry-account/0ki-napa-all.json")
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(), "fixtures/perry-account/0ki-marlin-none.json", HttpStatus.SC_OK);
    pushToCleanUpStack(assessment.getId(), "fixtures/perry-account/0ki-napa-all.json");
  }

  @Test
  public void postAssessment_success_noAssignmentSealedSameCounty() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-sealed.json",
        "fixtures/perry-account/account-napa-all.json",
        HttpStatus.SC_CREATED);
  }

  @Test
  public void postAssessment_success_noAssignmentSensitiveSameCounty() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-sensitive.json",
        "fixtures/perry-account/account-napa-all.json",
        HttpStatus.SC_CREATED);
  }

  @Test
  public void getAssessment_success_noAssignmentSealedSameCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sealed.json");
    assessment =
        postAssessmentAndGetResponse(assessment, "fixtures/perry-account/0ki-napa-all.json")
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(), "fixtures/perry-account/account-napa-all.json", HttpStatus.SC_OK);
    pushToCleanUpStack(assessment.getId(), "fixtures/perry-account/0ki-napa-all.json");
  }

  @Test
  public void getAssessment_success_noAssignmentSensitiveSameCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sensitive.json");
    assessment =
        postAssessmentAndGetResponse(assessment, "fixtures/perry-account/0ki-napa-all.json")
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(), "fixtures/perry-account/account-napa-all.json", HttpStatus.SC_OK);
    pushToCleanUpStack(assessment.getId(), "fixtures/perry-account/0ki-napa-all.json");
  }

  @Test
  public void postAssessment_failed_noAssignmentSealedDiffCounty() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-sealed.json",
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void postAssessment_failed_noAssignmentSensitiveDiffCounty() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-sensitive.json",
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
  }

  @Test
  public void getAssessment_failed_noAssignmentSealedDiffCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sealed.json");
    assessment =
        postAssessmentAndGetResponse(assessment, "fixtures/perry-account/0ki-napa-all.json")
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(),
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
    pushToCleanUpStack(assessment.getId(), "fixtures/perry-account/0ki-napa-all.json");
  }

  @Test
  public void getAssessment_failed_noAssignmentSensitiveDiffCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sensitive.json");
    assessment =
        postAssessmentAndGetResponse(assessment, "fixtures/perry-account/0ki-napa-all.json")
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(),
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
    pushToCleanUpStack(assessment.getId(), "fixtures/perry-account/0ki-napa-all.json");
  }

  @Test
  public void getAssessment_failed_noAssignmentDiffCounty() throws Exception {
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-sensitive.json");
    assessment =
        postAssessmentAndGetResponse(assessment, "fixtures/perry-account/0ki-napa-all.json")
            .readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(
        assessment.getId(),
        "fixtures/perry-account/el-dorado-all-authorized.json",
        HttpStatus.SC_FORBIDDEN);
    pushToCleanUpStack(assessment.getId(), "fixtures/perry-account/0ki-napa-all.json");
  }
}
