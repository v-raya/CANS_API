package gov.ca.cwds.cans.test.util;

import static gov.ca.cwds.cans.test.util.ObjectMapperProvider.OBJECT_MAPPER;

import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/** @author denys.davydov */
public final class FixtureReader {

  private FixtureReader() {}

  public static <T> T readObject(String fixturePath, Class<T> clazz) throws IOException {
    final String inputFixture = FixtureHelpers.fixture(fixturePath);
    return OBJECT_MAPPER.readValue(inputFixture, clazz);
  }

  public static Entity readRestObject(String fixturePath, Class clazz) throws IOException {
    final Object object = readObject(fixturePath, clazz);
    return Entity.entity(object, MediaType.APPLICATION_JSON_TYPE);
  }
}
