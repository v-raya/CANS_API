package gov.ca.cwds.cans.rest.resource;

import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import org.junit.Rule;

/**
 * @author denys.davydov
 */
public abstract class AbstractFunctionalTest {

  public static final String NOT_AUTHORIZED_ACCOUNT_FIXTURE = "fixtures/perry-account/zzz-not-authorized.json";
  public static final String AUTHORIZED_ACCOUNT_FIXTURE = "fixtures/perry-account/000-all-authorized.json";
  public static final String AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE =
      "fixtures/perry-account/el-dorado-all-authorized.json";
  public static final String AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/authorized-no-sealed.json";
  public static final String SENSITIVE_PERSONS_ACCOUNT_FIXTURE = "fixtures/perry-account/sensitive_persons-authorized.json";
  public static final String SEALED_ACCOUNT_FIXTURE = "fixtures/perry-account/sealed-authorized.json";
  public static final String NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE = "fixtures/perry-account/no_sealed_no_sensitive-authorized.json";
  public static final String FIXTURE_START = "fixtures/start-assessment-post.json";
  public static final String SLASH = "/";

  @Rule
  public AbstractRestClientTestRule clientTestRule = FunctionalTestContextHolder.clientTestRule;

}
