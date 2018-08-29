package gov.ca.cwds.cans;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.cans.rest.resource.AuthorizationResourceTest;
import gov.ca.cwds.cans.rest.resource.I18nResourceTest;
import gov.ca.cwds.cans.rest.resource.PersonResourceTest;
import gov.ca.cwds.cans.rest.resource.SecurityResourceTest;
import gov.ca.cwds.cans.rest.resource.SystemInformationResourceTest;
import gov.ca.cwds.cans.rest.resource.AssessmentResourceTest;
import gov.ca.cwds.cans.rest.resource.InstrumentResourceTest;
import gov.ca.cwds.cans.rest.resource.CountyResourceTest;
import gov.ca.cwds.cans.test.IntegrationRestClientTestRule;
import gov.ca.cwds.cans.test.util.ConfigurationProvider;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import java.security.KeyManagementException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author denys.davydov
 *     <p>The suite is run with "functionalTest" gradle task. It requires "api.url" and "perry.url"
 *     system properties to be set. The suite is used to test a remote environment.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  AuthorizationResourceTest.class,
  SystemInformationResourceTest.class,
  AssessmentResourceTest.class,
  CountyResourceTest.class,
  I18nResourceTest.class,
  InstrumentResourceTest.class,
  PersonResourceTest.class,
  SecurityResourceTest.class,
})
public class FunctionalTestSuite {

  static {
    JerseyGuiceUtils.install((s, serviceLocator) -> null);
  }

  @BeforeClass
  public static void init() throws KeyManagementException {
    FunctionalTestContextHolder.cansConfiguration = ConfigurationProvider.CONFIGURATION;
    FunctionalTestContextHolder.clientTestRule = new IntegrationRestClientTestRule();
  }
}
