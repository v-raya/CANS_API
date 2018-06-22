package gov.ca.cwds.cans.util;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;

/**
 * @author denys.davydov
 */
public class RequireTest {

  @Test
  public void require_doesNotThrow_whenNonNullObject() {
    Require.requireNotNullAndNotEmpty(Boolean.FALSE);
  }

  @Test(expected = NullOrEmptyException.class)
  public void require_throws_whenNullObject() {
    Require.requireNotNullAndNotEmpty((Boolean) null);
  }


  @Test
  public void require_doesNotThrow_whenNonBlankString() {
    Require.requireNotNullAndNotEmpty("123");
  }

  @Test(expected = NullOrEmptyException.class)
  public void require_throws_whenEmptyString() {
    Require.requireNotNullAndNotEmpty("");
  }

  @Test(expected = NullOrEmptyException.class)
  public void require_throws_whenNullString() {
    Require.requireNotNullAndNotEmpty((String) null);
  }


  @Test
  public void require_doesNotThrow_whenNonEmptyCollection() {
    final Collection<String> input = new ArrayList<>();
    input.add("123");
    Require.requireNotNullAndNotEmpty(input);
  }

  @Test(expected = NullOrEmptyException.class)
  public void require_throws_whenNullCollection() {
    Require.requireNotNullAndNotEmpty((Collection) null);
  }

  @Test(expected = NullOrEmptyException.class)
  public void require_throws_whenEmptyCollection() {
    Require.requireNotNullAndNotEmpty(new ArrayList());
  }
}