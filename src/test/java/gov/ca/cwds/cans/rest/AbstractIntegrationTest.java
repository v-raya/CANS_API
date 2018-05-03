package gov.ca.cwds.cans.rest;

import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import gov.ca.cwds.cans.test.util.IntegrationTestContextHolder;
import gov.ca.cwds.cans.test.util.DatabaseHelper;
import org.junit.Rule;

/**
 * @author denys.davydov
 */
public abstract class AbstractIntegrationTest {

  protected static final int UNPROCESSABLE_ENTITY_STATUS_CODE = 422;
  public static final String NOT_AUTHORIZED_ACCOUNT_FIXTURE = "fixtures/perry-account/zzz-not-authorized.json";
  public static final String AUTHORIZED_ACCOUNT_FIXTURE = "fixtures/perry-account/000-all-authorized.json";
  public static final String SLASH = "/";

  protected static final DatabaseHelper DATABASE_HELPER_CMS = new DatabaseHelper(
      IntegrationTestContextHolder.cansConfiguration.getDataSourceFactory()
  );

  @Rule
  public AbstractRestClientTestRule clientTestRule = IntegrationTestContextHolder.clientTestRule;

  public String transformDTOtoJSON(Object o) throws Exception {
    return clientTestRule.getMapper().writeValueAsString(o);
  }

}
