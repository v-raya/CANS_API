package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CHECK_PERMISSION;
import static gov.ca.cwds.cans.Constants.API.SECURITY;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class AssessmentResourceAuthorizationTest extends AbstractFunctionalTest {

  @Test
  public void postAssessment_success_whenUserHasAssignment() throws Exception {
    postAssessmentAndCheckStatus(
        "fixtures/client-of-0Ki-rw-assignment.json",
        "fixtures/perry-account/0ki-napa-none.json",
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
  public void postGetPutAssessment_hasAllowedOperations_whenUserHasReadOnlyAssignment()
      throws Exception {
    String[] allowedOperations = {"read", "update", "create", "complete", "write", "delete"};
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-r-assignment.json");
    assessment =
        postAssessmentAndGetResponse(assessment, AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .readEntity(AssessmentDto.class);
    checkOperations(assessment, allowedOperations);
    assessment =
        putAssessmentAndGetResponse(assessment, AUTHORIZED_NAPA_ACCOUNT_FIXTURE)
            .readEntity(AssessmentDto.class);
    checkOperations(assessment, allowedOperations);
    assessment =
        getAssessment(AUTHORIZED_NAPA_ACCOUNT_FIXTURE, assessment.getId())
            .readEntity(AssessmentDto.class);
    checkOperations(assessment, allowedOperations);
    pushToCleanUpStack(assessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);
  }

  @Test
  public void postAssessment_hasAllowedOperationsExceptComplete_whenUserHasntCompletePermission()
      throws Exception {
    String[] allowedOperations = {"read", "update", "create", "write", "delete"};
    AssessmentDto assessment = createAssessmentDto("fixtures/client-of-0Ki-r-assignment.json");
    assessment =
        postAssessmentAndGetResponse(
                assessment, "fixtures/perry-account/0ki-napa-all-no-assessment-complete.json")
            .readEntity(AssessmentDto.class);
    checkOperations(assessment, allowedOperations);
    pushToCleanUpStack(assessment.getId(), AUTHORIZED_NAPA_ACCOUNT_FIXTURE);
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

  @Test
  public void assessmentCreateAuth_unauthorized() throws Exception {
    final Boolean authorized =
        clientTestRule
            .withSecurityToken("fixtures/perry-account/0ki-napa-all.json")
            .target(SECURITY + "/" + CHECK_PERMISSION + "/client:createAssessment:Abxl9D005Y")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(Boolean.class);
    Assert.assertFalse(authorized);
  }

  @Test
  public void assessmentCreateAuth_authorized() throws Exception {
    final Boolean authorized =
        clientTestRule
            .withSecurityToken("fixtures/perry-account/0ki-napa-all.json")
            .target(SECURITY + "/" + CHECK_PERMISSION + "/client:createAssessment:O9kIYi80Ki")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(Boolean.class);
    Assert.assertTrue(authorized);
  }

  @Test
  public void assessmentCompleteAuth_authorized() throws Exception {
    final Boolean authorized =
        clientTestRule
            .withSecurityToken("fixtures/perry-account/0ki-napa-all.json")
            .target(SECURITY + "/" + CHECK_PERMISSION + "/client:completeAssessment:O9kIYi80Ki")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(Boolean.class);
    Assert.assertTrue(authorized);
  }

  @Test
  public void assessmentCompleteAuth_unauthorized() throws Exception {
    final Boolean authorized =
        clientTestRule
            .withSecurityToken("fixtures/perry-account/0ki-napa-all-no-assessment-complete.json")
            .target(SECURITY + "/" + CHECK_PERMISSION + "/client:completeAssessment:O9kIYi80Ki")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get()
            .readEntity(Boolean.class);
    Assert.assertFalse(authorized);
  }
}
