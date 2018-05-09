package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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

  @Test
  public void getI18n_returnsRecords_whenRecordsExist() throws IOException {
    // when
    final Map<String, String> actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.I18N + SLASH + "instrument.1" + SLASH + "en")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(Map.class);

    // then
    assertThat(actualResult.size(), is(not(0)));
  }

  @Test
  public void getI18n_returnsEmpty_whenWrongLanguage() throws IOException {
    // when
    final Map<String, String> actualResult = clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(API.I18N + SLASH + "instrument.1" + SLASH + "WRONG_LANGUAGE")
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
        .target(API.I18N + SLASH + "instrument.1")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get(Map.class);

    // then
    assertThat(actualResult.size(), is(not(0)));
  }
}
