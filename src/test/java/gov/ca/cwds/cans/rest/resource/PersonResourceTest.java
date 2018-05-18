package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;

import gov.ca.cwds.cans.domain.dto.PersonDto;
import java.io.IOException;
import org.junit.Test;

/**
 * @author denys.davydov
 */
public class PersonResourceTest extends AbstractCrudIntegrationTest<PersonDto> {

  @Override
  String getPostFixturePath() {
    return "fixtures/person-post.json";
  }

  @Override
  String getPutFixturePath() {
    return "fixtures/person-put.json";
  }

  @Override
  String getApiPath() {
    return PEOPLE;
  }

  @Test
  public void person_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }

}
