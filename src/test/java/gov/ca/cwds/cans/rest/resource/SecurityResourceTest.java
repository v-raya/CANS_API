package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.CHECK_PERMISSION;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SECURITY;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;

import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SecurityResourceTest extends AbstractFunctionalTest {

  private static final String PERSON_FIXTURE = "fixtures/person-post.json";
  private static final String ASSESSMENT_FIXTURE = "fixtures/assessment/assessment-post.json";
  private static final String SAME_COUNTY_USER =
      "fixtures/perry-account/el-dorado-all-authorized.json";
  private static final String DIFFERENT_COUNTY_USER = AUTHORIZED_ACCOUNT_FIXTURE;

  private AssessmentDto assessmentDto;
  private PersonResourceHelper personHelper;

  @Before
  public void before() throws Exception {
    personHelper = new PersonResourceHelper(clientTestRule);
    Entity<PersonDto> person = personHelper.readPersonEntity(PERSON_FIXTURE);
    PersonDto personDto = clientTestRule
        .withSecurityToken(SAME_COUNTY_USER)
        .target(PEOPLE)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(person)
        .readEntity(PersonDto.class);
    final AssessmentDto assessment = readObject(ASSESSMENT_FIXTURE, AssessmentDto.class);
    assessment.setPerson(personDto);
    assessmentDto = clientTestRule
        .withSecurityToken(SAME_COUNTY_USER)
        .target(ASSESSMENTS)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(assessment, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(AssessmentDto.class);
  }

  @After
  public void after() throws Exception {
    clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(ASSESSMENTS + SLASH + assessmentDto.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .delete();
    clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(PEOPLE + SLASH + assessmentDto.getPerson().getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .delete();
  }

  @Test
  public void testAuthorized() throws Exception {
    final Boolean authorized = clientTestRule
        .withSecurityToken(SAME_COUNTY_USER)
        .target(SECURITY + "/" + CHECK_PERMISSION + "/assessment:write:" + assessmentDto.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get()
        .readEntity(Boolean.class);
    Assert.assertTrue(authorized);
  }

  @Test
  public void testUnauthorized() throws Exception {
    final Boolean authorized = clientTestRule
        .withSecurityToken(DIFFERENT_COUNTY_USER)
        .target(SECURITY + "/" + CHECK_PERMISSION + "/assessment:write:" + assessmentDto.getId())
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get()
        .readEntity(Boolean.class);
    Assert.assertFalse(authorized);
  }

}
