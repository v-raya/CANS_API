package gov.ca.cwds.cans.domain.json;

import static gov.ca.cwds.cans.domain.enumeration.RatingType.NOT_APPLICABLE_RATING_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.cans.domain.enumeration.RatingType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/** @author denys.davydov */
public class ItemJsonValidationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void validate_success_whenValidItem() {
    final ItemJson validItem =
        new ItemJson()
            .setCode("code")
            .setAboveSixId("aboveSixId")
            .setComment("comment")
            .setRatingType(RatingType.REGULAR)
            .setRating(-1);
    final Set<ConstraintViolation<ItemJson>> violations = validator.validate(validItem);
    assertThat(violations.isEmpty(), is(true));
  }

  @Test
  public void validate_fail_whenCodeIsNull() {
    assertViolationMessage(new ItemJson(), "code", "may not be null");
  }

  @Test
  public void validate_fail_whenCodeIsEmpty() {
    assertViolationMessage(new ItemJson(), "code", "may not be empty");
  }

  @Test
  public void validate_fail_whenRatingTypeIsNull() {
    assertViolationMessage(new ItemJson(), "ratingType", "may not be null");
  }

  @Test
  public void validate_fail_whenAboveSixAndUnderSixAreNulls() {
    assertViolationMessage(
        new ItemJson(),
        "aboveSixId",
        "Either above_six_id or under_six_id field should have a value");
  }

  @Test
  public void validate_fail_whenHasRestrictedNaRating() {
    assertViolationMessage(
        new ItemJson()
            .setRatingType(RatingType.REGULAR)
            .setHasNaOption(Boolean.FALSE)
            .setRating(NOT_APPLICABLE_RATING_VALUE),
        "rating",
        "Item with rating_type = [REGULAR] with has_na_option = [false] can't have a rating = [8]");
  }

  @Test
  public void validate_fail_whenHasRestrictedRating() {
    assertViolationMessage(
        new ItemJson().setRatingType(RatingType.REGULAR).setRating(100),
        "rating",
        "Item with rating_type = [REGULAR] with has_na_option = [null] can't have a rating = [100]");
  }

  @Test
  public void validate_fail_whenCommentIsLongerThan250Symbols() {
    final ItemJson testSubject = new ItemJson().setComment(RandomStringUtils.random(251));
    assertViolationMessage(testSubject, "comment", "size must be between 0 and 250");
  }

  private void assertViolationMessage(
      final ItemJson testSubject, final String propertyPath, final String violationMessage) {
    final Set<ConstraintViolation<ItemJson>> violations = validator.validate(testSubject);
    final List<ConstraintViolation<ItemJson>> codeViolations =
        violations
            .stream()
            .filter(v -> propertyPath.equals(v.getPropertyPath().toString()))
            .filter(v -> violationMessage.equals(v.getMessage()))
            .collect(Collectors.toList());
    assertThat(codeViolations.size(), is(1));
  }
}
