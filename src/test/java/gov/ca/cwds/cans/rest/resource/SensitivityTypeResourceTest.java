package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/**
 * @author volodymyr.petrusha
 */
public class SensitivityTypeResourceTest extends AbstractFunctionalTest {

  public static final String STAFF_PERSON_COUNTY_ID = "1088";

  @Test
  public void getSensitivityTypes_AllPrivilege_success() throws IOException {
    // when
    final SensitivityType[] actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.SENSITIVITY_TYPES + "?county=" + STAFF_PERSON_COUNTY_ID)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(SensitivityType[].class);

    // then
    assertThat(actualResult.length, is(2));
  }

  @Test
  public void getSensitivityTypes_SensitivePrivilege_success() throws IOException {
    // when
    final SensitivityType[] actualResult = clientTestRule
        .withSecurityToken(SENSITIVE_PERSONS_ACCOUNT_FIXTURE)
        .target(API.SENSITIVITY_TYPES + "?county=" + STAFF_PERSON_COUNTY_ID)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(SensitivityType[].class);

    // then
    assertThat(actualResult.length, is(1));
    assertThat(actualResult[0], is(SensitivityType.SENSITIVE)); //1 is Id for sensitive type
  }

  @Test
  public void getSensitivityTypes_SealedPrivilege_success() throws IOException {
    // when
    final SensitivityType[] actualResult = clientTestRule
        .withSecurityToken(SEALED_ACCOUNT_FIXTURE)
        .target(API.SENSITIVITY_TYPES + "?county=" + STAFF_PERSON_COUNTY_ID)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(SensitivityType[].class);

    // then
    assertThat(actualResult.length, is(1));
    assertThat(actualResult[0], is(SensitivityType.SEALED)); //2 is Id for sealed type
  }

  @Test
  public void getSensitivityTypes_NoPrivileges_empty() throws IOException {
    // when
    final SensitivityType[] actualResult = clientTestRule
        .withSecurityToken(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE)
        .target(API.SENSITIVITY_TYPES + "?county=" + STAFF_PERSON_COUNTY_ID)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(SensitivityType[].class);

    // then
    assertThat(actualResult.length, is(0));
  }

  @Test
  public void getSensitivityTypes_other_county_empty() throws IOException {
    // when
    final SensitivityType[] actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.SENSITIVITY_TYPES + "?county=" + "1081")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(SensitivityType[].class);

    // then
    assertThat(actualResult.length, is(0));
  }
}
