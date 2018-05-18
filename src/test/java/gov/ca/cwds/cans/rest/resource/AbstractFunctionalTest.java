package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import gov.ca.cwds.cans.test.util.DatabaseHelper;
import org.junit.Rule;

/**
 * @author denys.davydov
 */
public abstract class AbstractFunctionalTest {

  public static final String NOT_AUTHORIZED_ACCOUNT_FIXTURE = "fixtures/perry-account/zzz-not-authorized.json";
  public static final String AUTHORIZED_ACCOUNT_FIXTURE = "fixtures/perry-account/000-all-authorized.json";
  public static final String SLASH = "/";

  protected static final DatabaseHelper DATABASE_HELPER_CANS = new DatabaseHelper(
      FunctionalTestContextHolder.cansConfiguration.getCansDataSourceFactory()
  );

  @Rule
  public AbstractRestClientTestRule clientTestRule = FunctionalTestContextHolder.clientTestRule;

}
