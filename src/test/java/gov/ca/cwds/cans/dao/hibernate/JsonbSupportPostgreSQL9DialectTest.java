package gov.ca.cwds.cans.dao.hibernate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

/**
 * @author denys.davydov
 */
public class JsonbSupportPostgreSQL9DialectTest {

  @Test
  public void constructor_doesNotFail() {
    final JsonbSupportPostgreSQL9Dialect actual = new JsonbSupportPostgreSQL9Dialect();
    assertThat(actual, is(notNullValue()));
  }
}