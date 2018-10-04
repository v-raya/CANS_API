package gov.ca.cwds.cans.test;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.cans.test.util.TestUtils;
import gov.ca.cwds.cans.util.Require;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;

/** @author denys.davydov */
public class SmokeRestClientTestRule extends AbstractRestClientTestRule {

  public SmokeRestClientTestRule() {
    mapper = Jackson.newObjectMapper();
    ObjectMapperUtils.configureObjectMapper(mapper);
    apiUrl = TestUtils.getApiUrl();
    Require.requireNotNullAndNotEmpty(apiUrl);
  }

  @Override
  String generateToken(String identity, String password) throws IOException {
    return null; // not needed for smoke testing
  }
}
