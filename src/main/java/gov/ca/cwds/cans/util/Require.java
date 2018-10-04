package gov.ca.cwds.cans.util;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

/**
 * The util class is intended to validate method parameters on not being null or empty. {@link
 * NullOrEmptyException} is thrown when requirement is not met.
 *
 * @author CWDS TPT-3 Team
 */
public final class Require {

  private Require() {}

  public static void requireNotNullAndNotEmpty(final String input) {
    if (StringUtils.isEmpty(input)) {
      throw new NullOrEmptyException(createMessage(input));
    }
  }

  public static void requireNotNullAndNotEmpty(final Collection input) {
    if (input == null || input.isEmpty()) {
      throw new NullOrEmptyException(createMessage(input));
    }
  }

  public static void requireNotNullAndNotEmpty(final Object input) {
    if (input == null) {
      throw new NullOrEmptyException(createMessage(null));
    }
  }

  private static String createMessage(final Object o) {
    return String.format("Value = {%s}", o);
  }
}
