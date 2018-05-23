package gov.ca.cwds.cans.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.ca.cwds.ObjectMapperUtils;

/** @author denys.davydov */
public final class ObjectMapperProvider {
  private ObjectMapperProvider() {}

  public static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtils.createObjectMapper();

  static {
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
  }
}
