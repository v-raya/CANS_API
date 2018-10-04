package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import java.io.IOException;
import java.util.Stack;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssessmentResourceAuthorizationTest extends AbstractFunctionalTest {

  private static final String FIXTURE_POST_ELDORADO_PERSON = "fixtures/person-post.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";

  private PersonResourceHelper personHelper;

  private final Stack<AssessmentDto> cleanUpAssessments = new Stack<>();

  @After
  public void tearDown() throws IOException {
    personHelper.cleanUp();
    while (!cleanUpAssessments.empty()) {
      AssessmentDto assessmentToDelete = cleanUpAssessments.pop();
      clientTestRule
          .withSecurityToken(
              personHelper.findUserAccountForDelete(assessmentToDelete.getPerson().getCounty()))
          .target(ASSESSMENTS + SLASH + assessmentToDelete.getId())
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
  }

  @Before
  public void before() {
    personHelper = new PersonResourceHelper(clientTestRule);
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void putAssessmentForSensitivePerson_success_whenUserHasSensitivePrivilege()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        putAssessment(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE, postedAssessment).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_OK));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  @Test
  public void putAssessmentForSensitivePerson_unauthorize_whenUserDoesntHaveSensitivePrivilege()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status =
        putAssessment(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE, postedAssessment).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_FORBIDDEN));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  @Test
  public void putAssessmentForSensitivePerson_unauthorize_whenUserNotTheSameCounty()
      throws IOException {
    // given
    final AssessmentDto postedAssessment = postAssessmentForSensitivePerson();
    // when
    final int status = putAssessment(AUTHORIZED_ACCOUNT_FIXTURE, postedAssessment).getStatus();
    // then
    assertThat(status, is(HttpStatus.SC_FORBIDDEN));
    // clean up
    cleanUpAssessments.add(postedAssessment);
  }

  private AssessmentDto postAssessmentForSensitivePerson() throws IOException {
    final PersonDto person = personHelper.readPersonDto(FIXTURE_POST_ELDORADO_PERSON);
    person.setSensitivityType(SensitivityType.SENSITIVE);
    final PersonDto postedPerson =
        personHelper.postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    assessment.setPerson(postedPerson);
    return postAssesment(assessment);
  }

  private AssessmentDto postAssesment(AssessmentDto assessment) throws IOException {
    AssessmentDto postedAssessment =
        clientTestRule
            .withSecurityToken(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE)
            .target(ASSESSMENTS)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(AssessmentDto.class);
    cleanUpAssessments.push(assessment);
    return postedAssessment;
  }

  private Response putAssessment(String accountFixture, AssessmentDto assessment)
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
}
