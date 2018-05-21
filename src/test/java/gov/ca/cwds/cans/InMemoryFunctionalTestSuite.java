package gov.ca.cwds.cans;

import static gov.ca.cwds.cans.test.util.ConfigurationProvider.CONFIG_FILE_PATH;

import gov.ca.cwds.cans.rest.resource.AssessmentResourceTest;
import gov.ca.cwds.cans.rest.resource.CountyResourceTest;
import gov.ca.cwds.cans.rest.resource.I18nResourceTest;
import gov.ca.cwds.cans.rest.resource.InstrumentResourceTest;
import gov.ca.cwds.cans.rest.resource.PersonResourceTest;
import gov.ca.cwds.cans.rest.resource.SystemInformationResourceTest;
import gov.ca.cwds.cans.test.InMemoryFunctionalRestClientTestRule;
import gov.ca.cwds.cans.test.util.DatabaseHelper;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import gov.ca.cwds.cans.util.DbUpgrader;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.IOException;
import javax.ws.rs.client.Client;
import liquibase.exception.LiquibaseException;
import org.glassfish.jersey.client.JerseyClient;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author denys.davydov
 *     <p>The suite is a part of unit tests. All the tests with "ResourceTest" postfix are excluded
 *     from default junit tests running and must be added to this suite. The suite sets up
 *     dropwizard app and inmemory db once for all the "ResourceTest" tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  SystemInformationResourceTest.class,
  AssessmentResourceTest.class,
  CountyResourceTest.class,
  I18nResourceTest.class,
  InstrumentResourceTest.class,
  PersonResourceTest.class,
})
public class InMemoryFunctionalTestSuite {

  @ClassRule
  public static final DropwizardAppRule<CansConfiguration> DROPWIZARD_APP_RULE =
      new DropwizardAppRule<CansConfiguration>(
          CansApplication.class, ResourceHelpers.resourceFilePath(CONFIG_FILE_PATH)) {
        @Override
        public Client client() {
          Client client = super.client();
          if (((JerseyClient) client).isClosed()) {
            client = clientBuilder().build();
          }
          return client;
        }
      };

  @BeforeClass
  public static void init() throws Exception {
    final CansConfiguration configuration = DROPWIZARD_APP_RULE.getConfiguration();
    FunctionalTestContextHolder.cansConfiguration = configuration;
    FunctionalTestContextHolder.clientTestRule =
        new InMemoryFunctionalRestClientTestRule(DROPWIZARD_APP_RULE);
    initCansDb();
    DbUpgrader.runDmlOnCansDb(configuration);
  }

  private static void initCansDb() throws LiquibaseException {
    try (final DatabaseHelper databaseHelper = createCansDbHelper()) {
      databaseHelper.runScript("liquibase/cans_database_master.xml");
    } catch (IOException e) {
      throw new LiquibaseException(e);
    }
  }

  private static DatabaseHelper createCansDbHelper() {
    return new DatabaseHelper(
        FunctionalTestContextHolder.cansConfiguration.getCansDataSourceFactory());
  }
}
