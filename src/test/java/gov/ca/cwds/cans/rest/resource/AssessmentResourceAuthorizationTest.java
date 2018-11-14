package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.CountyDto;
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

  private static final String FIXTURE_POST_ELDORADO_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  private static final String SENSITIVE_CLIENT_IDENTIFIER = "AbA4BJy0Aq";
  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();

  @After
  public void tearDown() throws IOException {
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(
              findUserAccountForDelete(assessmentToDelete.getPerson().getCounty()))
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

  //@Test
  public void getAssessment_success_whenUserHasReadOnlyAssignment() throws Exception {
    AssessmentDto assessment = createAssessmentDto(FIXTURE_POST_ASSESSMENT);
    assessment = postAssessmentAndGetResponse(assessment,
        "fixtures/client-of-0Ki-rw-assignment.json").readEntity(AssessmentDto.class);
    getAssessmentAndCheckStatus(assessment.getId(), "fixtures/perry-account/0ki-marlin-none.json",
        HttpStatus.SC_OK);

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

  public void getAssessmentForSensitivePerson_success_whenUserHasSensitivePrivilege()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        getAssessment(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE, postedAssessment.getId()).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_OK));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  public void getAssessmentForSensitivePerson_unauthorize_whenUserDoesntHaveSensitivePrivilege()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        getAssessment(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE, postedAssessment.getId()).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_FORBIDDEN));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  public void getAssessmentForSensitivePerson_unauthorize_whenUserNotTheSameCounty()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        getAssessment(AUTHORIZED_ACCOUNT_FIXTURE, postedAssessment.getId()).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_FORBIDDEN));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  public void putAssessmentForSensitivePerson_success_whenUserHasSensitivePrivilege()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        putAssessment(postedAssessment, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_OK));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  public void putAssessmentForSensitivePerson_unauthorize_whenUserDoesntHaveSensitivePrivilege()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        putAssessment(postedAssessment, NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_FORBIDDEN));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  public void putAssessmentForSensitivePerson_unauthorize_whenUserNotTheSameCounty()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status = putAssessment(postedAssessment, AUTHORIZED_ACCOUNT_FIXTURE).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_FORBIDDEN));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }


  private AssessmentDto postAssessmentForSensitivePerson() throws IOException {
    final ClientDto client = readObject(FIXTURE_POST_ELDORADO_PERSON, ClientDto.class);
    client.setIdentifier(SENSITIVE_CLIENT_IDENTIFIER);
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    assessment.setPerson(client);
    return postAssessment(assessment, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
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

  private AssessmentDto postAssessment(AssessmentDto assessment, String userFixture)
      throws IOException {
    AssessmentDto postedAssessment = postAssessmentAndGetResponse(assessment, userFixture)
        .readEntity(AssessmentDto.class);
    cleanUpAssessments.push(assessment);
    return postedAssessment;
  }

  private Response postAssessmentAndGetResponse(AssessmentDto assessment, String userFixture)
      throws IOException {
    return clientTestRule
        .withSecurityToken(userFixture)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE));
  }

  private Response putAssessment(AssessmentDto assessment, String accountFixture)
      throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(ASSESSMENTS + SLASH + assessment.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .put(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE));
  }

  private Response getAssessment(String accountFixture, Long id) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(ASSESSMENTS + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();
  }

  private String findUserAccountForDelete(CountyDto county) {
    if (county == null) {
      return AUTHORIZED_ACCOUNT_FIXTURE;
    }
    switch (county.getName()) {
      case "El Dorado":
        return AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE;
      case "Marin":
        return AUTHORIZED_ACCOUNT_FIXTURE;
      case "San Luis Obispo":
        return SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED;
      case "Napa":
        return AUTHORIZED_NAPA_ACCOUNT_FIXTURE;
      default:
        throw new IllegalArgumentException(
            "There is no account fixture for county: " + county.getName());
    }
  }
}
