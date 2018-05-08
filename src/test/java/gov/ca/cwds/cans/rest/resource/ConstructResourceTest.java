package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CONSTRUCTS;

import gov.ca.cwds.cans.domain.dto.ConstructDto;
import java.io.IOException;
import org.junit.Test;

/** @author denys.davydov */
public class ConstructResourceTest extends AbstractCrudIntegrationTest<ConstructDto> {

  @Override
  String getPostFixturePath() {
    return "fixtures/construct-post.json";
  }

  @Override
  String getPutFixturePath() {
    return "fixtures/construct-put.json";
  }

  @Override
  String getApiPath() {
    return CONSTRUCTS;
  }

  @Test
  public void construct_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }
}
