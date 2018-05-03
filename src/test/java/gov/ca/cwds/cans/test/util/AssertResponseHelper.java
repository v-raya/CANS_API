package gov.ca.cwds.cans.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.util.Map;

/** @author denys.davydov */
public final class AssertResponseHelper {

  private AssertResponseHelper() {}

  @SuppressWarnings("unchecked")
  public static void assertEqualsResponse(String fixture, String actualString,
      String... excludedProperties) throws IOException {
    ObjectMapper objectMapper = Jackson.newObjectMapper();
    Map<String, String> expectedMap =
        (Map<String, String>) objectMapper.readValue(fixture, Map.class);
    Map<String, String> actualMap =
        (Map<String, String>) objectMapper.readValue(actualString, Map.class);

    for(String excludedProperty : excludedProperties) {
      expectedMap.remove(excludedProperty);
      actualMap.remove(excludedProperty);
    }
    assertThat(actualMap).isEqualTo(expectedMap);
  }
}
