package gov.ca.cwds.cans;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.cans.test.SmokeRestClientTestRule;
import gov.ca.cwds.cans.test.util.ConfigurationProvider;
import gov.ca.cwds.cans.test.util.IntegrationTestContextHolder;
import gov.ca.cwds.cans.rest.SystemInformationResourceTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author denys.davydov
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    SystemInformationResourceTest.class
})
public class SmokeTestSuite {

  static {
    JerseyGuiceUtils.install((s, serviceLocator) -> null);
  }

  @BeforeClass
  public static void init() {
    IntegrationTestContextHolder.cansConfiguration = ConfigurationProvider.CONFIGURATION;
    IntegrationTestContextHolder.clientTestRule = new SmokeRestClientTestRule();
  }
}
