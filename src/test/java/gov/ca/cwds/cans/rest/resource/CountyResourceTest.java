package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.test.util.AssertFixtureUtils.assertResponseByFixturePath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.rest.AbstractIntegrationTest;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/**
 * @author denys.davydov
 */
public class CountyResourceTest extends AbstractIntegrationTest {

//  private static final String LIQUIBASE_SCRIPT = "liquibase/address/dml_address_test_data.xml";
//
//  @BeforeClass
//  public static void beforeClass() throws Exception {
//    DATABASE_HELPER_CANS.runScripts(LIQUIBASE_SCRIPT);
//  }
//
//  @AfterClass
//  public static void afterClass() throws Exception {
//    DATABASE_HELPER_CANS.rollbackScripts(LIQUIBASE_SCRIPT);
//  }

  @Test
  public void getAllCounties_success() throws IOException {
    // when
    final County[] actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.COUNTIES)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(County[].class);

    // then
    assertThat(actualResult.length, is(not(0)));
  }
}
