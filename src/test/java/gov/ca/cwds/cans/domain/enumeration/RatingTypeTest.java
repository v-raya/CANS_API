package gov.ca.cwds.cans.domain.enumeration;

import static gov.ca.cwds.cans.domain.enumeration.RatingType.BOOLEAN;
import static gov.ca.cwds.cans.domain.enumeration.RatingType.REGULAR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/** @author denys.davydov */
public class RatingTypeTest {

  @Test
  public void isValidRating_returnsTrue_whenBooleanRatingIsValid() {
    assertThat(BOOLEAN.isValidRating(-1), is(true));
    assertThat(BOOLEAN.isValidRating(0), is(true));
    assertThat(BOOLEAN.isValidRating(1), is(true));
  }

  @Test
  public void isValidRating_returnsFalse_whenInvalidBooleanRating() {
    assertThat(BOOLEAN.isValidRating(null), is(false));
    assertThat(BOOLEAN.isValidRating(-100), is(false));
    assertThat(BOOLEAN.isValidRating(-2), is(false));
    assertThat(BOOLEAN.isValidRating(2), is(false));
    assertThat(BOOLEAN.isValidRating(3), is(false));
    assertThat(BOOLEAN.isValidRating(100), is(false));
  }

  @Test
  public void isValidRating_returnsTrue_whenRegularRatingIsValid() {
    assertThat(REGULAR.isValidRating(-1), is(true));
    assertThat(REGULAR.isValidRating(0), is(true));
    assertThat(REGULAR.isValidRating(1), is(true));
    assertThat(REGULAR.isValidRating(2), is(true));
    assertThat(REGULAR.isValidRating(3), is(true));
  }

  @Test
  public void isValidRating_returnsFalse_whenInvalidRegularRating() {
    assertThat(REGULAR.isValidRating(null), is(false));
    assertThat(REGULAR.isValidRating(-100), is(false));
    assertThat(REGULAR.isValidRating(-2), is(false));
    assertThat(REGULAR.isValidRating(4), is(false));
    assertThat(REGULAR.isValidRating(100), is(false));
  }
}
