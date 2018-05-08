package gov.ca.cwds.cans;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.cans.rest.SystemInformationResourceTest;
import gov.ca.cwds.cans.rest.resource.AssessmentResourceTest;
import gov.ca.cwds.cans.rest.resource.ConstructResourceTest;
import gov.ca.cwds.cans.rest.resource.CountyResourceTest;
import gov.ca.cwds.cans.test.IntegrationRestClientTestRule;
import gov.ca.cwds.cans.test.util.ConfigurationProvider;
import gov.ca.cwds.cans.test.util.IntegrationTestContextHolder;
import java.security.KeyManagementException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author denys.davydov
 *     <p>The suite is run with "integrationTest" gradle task. It requires "api.url" and "perry.url"
 *     system properties to be set. The suite is used to test a remote environment.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  SystemInformationResourceTest.class,
  AssessmentResourceTest.class,
  ConstructResourceTest.class,
  CountyResourceTest.class,
})
public class IntegrationTestSuite {

  static {
    JerseyGuiceUtils.install((s, serviceLocator) -> null);
  }

  @BeforeClass
  public static void init() throws KeyManagementException {
    IntegrationTestContextHolder.cansConfiguration = ConfigurationProvider.CONFIGURATION;
    IntegrationTestContextHolder.clientTestRule = new IntegrationRestClientTestRule();
  }
}
