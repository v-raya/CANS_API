package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.test.AbstractRestClientTestRule;
import gov.ca.cwds.cans.test.util.FunctionalTestContextHolder;
import javax.ws.rs.core.Response;
import org.junit.Rule;

/** @author denys.davydov */
public abstract class AbstractFunctionalTest {

  public static final String NOT_AUTHORIZED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/zzz-not-authorized.json";
  public static final String AUTHORIZED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/000-all-authorized.json";
  public static final String AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE =
      "fixtures/perry-account/el-dorado-all-authorized.json";
  public static final String AUTHORIZED_NO_SEALED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/authorized-no-sealed.json";
  public static final String SENSITIVE_PERSONS_ACCOUNT_FIXTURE =
      "fixtures/perry-account/sensitive_persons-authorized.json";
  public static final String SEALED_ACCOUNT_FIXTURE =
      "fixtures/perry-account/sealed-authorized.json";
  public static final String SEALED_EL_DORADO_ACCOUNT_FIXTURE =
      "fixtures/perry-account/el-dorado-sealed-authorized.json";
  public static final String NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE =
      "fixtures/perry-account/no_sealed_no_sensitive-authorized.json";
  public static final String STATE_OF_CA_ALL_AUTHORIZED =
      "fixtures/perry-account/state-of-california-all-authorized.json";
  public static final String STATE_OF_CA_NO_SENSITIVITY =
      "fixtures/perry-account/state-of-california-no-sensitivity-no-sealed.json";
  public static final String FIXTURE_START = "fixtures/start-assessment-post.json";
  public static final String SLASH = "/";
  static final String AUTHORIZED_ACCOUNT_SINGLE_COUNTY_FIXTURE =
      "fixtures/perry-account/single-county-authorized.json";
  private static final String EDITABLE = "editable";

  @Rule
  public AbstractRestClientTestRule clientTestRule = FunctionalTestContextHolder.clientTestRule;

  protected void checkMetadataEditable(Response response, boolean metadataEditable) {
    PersonDto personDto = response.readEntity(PersonDto.class);
    assertNotNull(personDto.getMetadata());
    assertThat(personDto.getMetadata().get(EDITABLE), is(metadataEditable));
  }
}
