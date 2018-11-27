package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import org.apache.http.HttpStatus;
import org.junit.Test;

public class AssessmentResourceAuthorizationTest extends AbstractFunctionalTest {

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
}
