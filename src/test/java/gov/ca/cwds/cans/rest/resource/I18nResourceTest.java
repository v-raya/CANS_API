package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
/**
 * @author denys.davydov
 */
public class I18nResourceTest extends AbstractIntegrationTest {

  private static final String KEY_PREFIX = "instrument.1.";

  @Test
  public void getI18n_returnsRecords_whenRecordsExist() throws IOException {
    // when
    final Map<String, String> actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.I18N + SLASH + KEY_PREFIX + SLASH + "en")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(Map.class);

    // then
    assertThat(actualResult.size(), is(not(0)));
    final String firstActualKey = actualResult.entrySet().iterator().next().getKey();
    assertThat(firstActualKey, startsWith(KEY_PREFIX));
  }

  @Test
  public void getI18n_returnsEmpty_whenWrongLanguage() throws IOException {
    // when
    final Map<String, String> actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.I18N + SLASH + KEY_PREFIX + SLASH + "WRONG_LANGUAGE")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(Map.class);

    // then
    assertThat(actualResult.size(), is(0));
  }

  @Test
  public void getI18n_returnsRecords_whenNoLanguageAndDefaultEnIsSet() throws IOException {
    // when
    final Map<String, String> actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.I18N + SLASH + KEY_PREFIX)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(Map.class);

    // then
    assertThat(actualResult.size(), is(not(0)));
  }
}
