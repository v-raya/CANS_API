package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CONSTRUCTS;

import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import java.io.IOException;
import org.junit.Test;

/** @author denys.davydov */
public class InstrumentResourceTest extends AbstractCrudIntegrationTest<InstrumentDto> {

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
    return CONSTRUCTS;
  }

  @Test
  public void instrument_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }
}
