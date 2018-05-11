package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.I18N;
import static gov.ca.cwds.cans.Constants.API.INSTRUMENTS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import liquibase.exception.LiquibaseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author denys.davydov */
public class InstrumentResourceTest extends AbstractCrudIntegrationTest<InstrumentDto> {

  private static final String LIQUIBASE_SCRIPT = "liquibase/instrument_insert.xml";

  @BeforeClass
  public static void onBeforeClass() throws LiquibaseException {
    DATABASE_HELPER_CANS.runScripts(LIQUIBASE_SCRIPT);
  }

  @AfterClass
  public static void onAfterClass() throws LiquibaseException {
    DATABASE_HELPER_CANS.rollbackScripts(LIQUIBASE_SCRIPT);
  }

  @Override
  String getPostFixturePath() {
    return "fixtures/instrument-post.json";
  }

  @Override
  String getPutFixturePath() {
    return "fixtures/instrument-put.json";
  }

  @Override
  String getApiPath() {
    return INSTRUMENTS;
  }

  @Test
  public void instrument_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }

  @Test
  public void getInstrumentsI18n_returnsRecords_whenRecordsExist() throws IOException {
    // given
    final long instrumentId = 49999;

    // when
    final Map<String, String> actualResult =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.INSTRUMENTS + SLASH + instrumentId + SLASH + I18N + SLASH + "en")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(Map.class);

    // then
    assertThat(actualResult.size(), is(not(0)));
    assertThat(actualResult.keySet(), containsInAnyOrder("_title_", "Domain1._title_"));
  }

}
