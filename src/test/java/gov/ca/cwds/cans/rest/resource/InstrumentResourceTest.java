package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.I18N;
import static gov.ca.cwds.cans.Constants.API.INSTRUMENTS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/** @author denys.davydov */
public class InstrumentResourceTest extends AbstractCrudFunctionalTest<InstrumentDto> {

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
    final long instrumentId = 1;

    // when
    final Map<String, String> actualResult =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(API.INSTRUMENTS + SLASH + instrumentId + SLASH + I18N + SLASH + "en")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(Map.class);

    // then
    assertThat(actualResult.size(), is(not(0)));
  }
}
