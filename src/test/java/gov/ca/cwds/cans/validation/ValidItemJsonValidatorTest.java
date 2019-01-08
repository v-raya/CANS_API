package gov.ca.cwds.cans.validation;

import static gov.ca.cwds.cans.domain.enumeration.RatingType.BOOLEAN;
import static gov.ca.cwds.cans.domain.enumeration.RatingType.NOT_APPLICABLE_RATING_VALUE;
import static gov.ca.cwds.cans.domain.enumeration.RatingType.REGULAR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.cans.domain.json.ItemJson;
import org.junit.Test;

/** @author denys.davydov */
public class ValidItemJsonValidatorTest {

  private final ValidItemJsonValidator testSubject = new ValidItemJsonValidator();

  @Test
  public void isValid_returnsTrue_whenNullInput() {
    final boolean actual = testSubject.isValid(null, null);
    assertThat(actual, is(true));
  }

  @Test
  public void isValid_returnsTrue_whenValidInput() {
    final boolean actual = testSubject.isValid(createValidItemJson(), null);
    assertThat(actual, is(true));
  }

  @Test
  public void isValid_returnsFalse_whenAboveSixIdAndUnderSixIdAreNulls() {
    final ItemJson input = new ItemJson().setAboveSixId(null).setUnderSixId(null);
    final boolean actual = testSubject.isValid(input, null);
    assertThat(actual, is(false));
  }

  @Test
  public void isValid_returnsTrue_whenHasPermittedNaRating() {
    final ItemJson input =
        createValidItemJson().setHasNaOption(Boolean.TRUE).setRating(NOT_APPLICABLE_RATING_VALUE);
    final boolean actual = testSubject.isValid(input, null);
    assertThat(actual, is(true));
  }

  @Test
  public void isValid_returnsFalse_whenHasNotPermittedNaRating() {
    final ItemJson input = createValidItemJson().setRating(NOT_APPLICABLE_RATING_VALUE);
    final boolean actual = testSubject.isValid(input, null);
    assertThat(actual, is(false));
  }

  @Test
  public void isValid_returnsTrue_whenHasValidRegularRating() {
    final ItemJson itemJson = createValidItemJson().setRatingType(REGULAR);
    assertThat(testSubject.isValid(itemJson.setRating(-1), null), is(true));
    assertThat(testSubject.isValid(itemJson.setRating(0), null), is(true));
    assertThat(testSubject.isValid(itemJson.setRating(1), null), is(true));
    assertThat(testSubject.isValid(itemJson.setRating(2), null), is(true));
    assertThat(testSubject.isValid(itemJson.setRating(3), null), is(true));
  }

  @Test
  public void isValid_returnsFalse_whenHasInvalidRegularRating() {
    final ItemJson itemJson = createValidItemJson().setRatingType(REGULAR);
    assertThat(testSubject.isValid(itemJson.setRating(null), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(-100), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(-2), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(4), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(1000), null), is(false));
  }

  @Test
  public void isValid_returnsTrue_whenHasValidBooleanRating() {
    final ItemJson itemJson = createValidItemJson().setRatingType(BOOLEAN);
    assertThat(testSubject.isValid(itemJson.setRating(-1), null), is(true));
    assertThat(testSubject.isValid(itemJson.setRating(0), null), is(true));
    assertThat(testSubject.isValid(itemJson.setRating(1), null), is(true));
  }

  @Test
  public void isValid_returnsFalse_whenHasInvalidBooleanRating() {
    final ItemJson itemJson = createValidItemJson().setRatingType(BOOLEAN);
    assertThat(testSubject.isValid(itemJson.setRating(null), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(-100), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(-2), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(2), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(3), null), is(false));
    assertThat(testSubject.isValid(itemJson.setRating(1000), null), is(false));
  }

  private ItemJson createValidItemJson() {
    return new ItemJson().setAboveSixId("aboveSixId").setRatingType(BOOLEAN).setRating(-1);
  }
}
