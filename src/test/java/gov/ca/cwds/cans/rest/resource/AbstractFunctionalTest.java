package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import java.io.IOException;
import java.util.Stack;
import javafx.util.Pair;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;

/** @author denys.davydov */
public abstract class AbstractFunctionalTest {

  public static final String NOT_AUTHORIZED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/zzz-not-authorized.json";
  public static final String NO_CLIENT_READ_ACCOUNT_FIXTURE =
      "fixtures/perry-account/zzz-no-client-read-permission.json";
  public static final String AUTHORIZED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/000-all-authorized.json";
  public static final String SUPERVISOR_SAN_LOUIS_ALL_AUTHORIZED =
      "fixtures/perry-account/supervisor-san-louis-all-authorized.json";
  public static final String SUPERVISOR_SAN_LOUIS_NO_PERMISSION =
      "fixtures/perry-account/supervisor-san-louis-no-permission.json";
  public static final String AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE =
      "fixtures/perry-account/el-dorado-all-authorized.json";
  public static final String AUTHORIZED_NAPA_ACCOUNT_FIXTURE =
      "fixtures/perry-account/0ki-napa-all.json";
  public static final String AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/authorized-no-sealed.json";
  public static final String SENSITIVE_PERSONS_ACCOUNT_FIXTURE =
      "fixtures/perry-account/sensitive_persons-authorized.json";
  public static final String SEALED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/sealed-authorized.json";
  public static final String SEALED_EL_DORADO_ACCOUNT_FIXTURE =
      "fixtures/perry-account/el-dorado-sealed-authorized.json";
  public static final String NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE =
      "fixtures/perry-account/no_sealed_no_sensitive-authorized.json";
  public static final String STATE_OF_CA_ALL_AUTHORIZED =
      "fixtures/perry-account/state-of-california-all-authorized.json";
  public static final String STATE_OF_CA_NO_SENSITIVITY =
      "fixtures/perry-account/state-of-california-no-sensitivity-no-sealed.json";
  private static final String FIXTURE_POST_ASSESSMENT = "fixtures/assessment/assessment-post.json";
  final Stack<Pair<Long, String>> cleanUpAssessmentsToUserFixtures = new Stack<>();

  public static final String SLASH = "/";
  private static final String EDITABLE = "editable";

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

  @Rule
  public AbstractRestClientTestRule clientTestRule = FunctionalTestContextHolder.clientTestRule;

  protected void checkMetadataEditable(Response response, boolean metadataEditable) {
    PersonDto personDto = response.readEntity(PersonDto.class);
    assertNotNull(personDto.getMetadata());
    assertThat(personDto.getMetadata().get(EDITABLE), is(metadataEditable));
  }

  void postAssessmentAndCheckStatus(String personFixture, String userFixture, int expectedStatus)
      throws Exception {
    AssessmentDto assessment = createAssessmentDto(personFixture);
    Response response = postAssessmentAndGetResponse(assessment, userFixture);
    checkStatus(response, userFixture, expectedStatus);
  }

  private void checkStatus(Response response, String userFixture, int expectedStatus) {
    int actualStatus = response.getStatus();
    if (actualStatus < 300) {
      AssessmentDto postedAssessment = response.readEntity(AssessmentDto.class);
      pushToCleanUpStack(postedAssessment.getId(), userFixture);
    }
    Assert.assertEquals(expectedStatus, actualStatus);
  }

  AssessmentDto createAssessmentDto(String personFixture) throws Exception {
    final AssessmentDto assessment = readObject(FIXTURE_POST_ASSESSMENT, AssessmentDto.class);
    final ClientDto person = readObject(personFixture, ClientDto.class);
    assessment.setPerson(person);
    return assessment;
  }

  Response postAssessmentAndGetResponse(AssessmentDto assessment, String userFixture)
      throws IOException {
    return clientTestRule
        .withSecurityToken(userFixture)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE));
  }

  void getAssessmentAndCheckStatus(Long id, String userFixture, int expectedStatus)
      throws IOException {
    int actualStatus = getAssessment(userFixture, id).getStatus();
    Assert.assertEquals(expectedStatus, actualStatus);
  }

  void pushToCleanUpStack(Long assessmentId, String userFixture) {
    cleanUpAssessmentsToUserFixtures.push(new Pair<>(assessmentId, userFixture));
  }

  private Response getAssessment(String accountFixture, Long id) throws IOException {
    return clientTestRule
        .withSecurityToken(accountFixture)
        .target(ASSESSMENTS + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();
  }

  void getClient(String agJLkWe0Ki, String securityToken, int httpStatus) throws IOException {
    Response response =
        clientTestRule
            .withSecurityToken(securityToken)
            .target(API.CLIENTS + SLASH + agJLkWe0Ki)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
    Assert.assertThat(response.getStatus(), Matchers.equalTo(httpStatus));
  }
}
