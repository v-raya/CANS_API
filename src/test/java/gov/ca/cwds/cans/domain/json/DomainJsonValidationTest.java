package gov.ca.cwds.cans.domain.json;

import static java.util.Collections.singletonList;
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
public class DomainJsonValidationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void validate_success_whenValidDomain() {
    final DomainJson validDomain =
        new DomainJson()
            .setCode("code")
            .setAboveSix(true)
            .setComment("comment")
            .setCaregiverName("Caregiver Name")
            .setItems(
                singletonList(
                    new ItemJson()
                        .setCode("code")
                        .setAboveSixId("id")
                        .setRatingType(RatingType.REGULAR)
                        .setRating(-1)));
    final Set<ConstraintViolation<DomainJson>> violations = validator.validate(validDomain);
    assertThat(violations.isEmpty(), is(true));
  }

  @Test
  public void validate_fail_whenCodeIsNull() {
    assertViolationMessage(new DomainJson(), "code", "may not be null");
  }

  @Test
  public void validate_fail_whenCodeIsEmpty() {
    assertViolationMessage(new DomainJson(), "code", "may not be empty");
  }

  @Test
  public void validate_fail_whenItemsIsNull() {
    assertViolationMessage(new DomainJson().setItems(null), "items", "may not be null");
  }

  @Test
  public void validate_fail_whenItemsIsEmpty() {
    assertViolationMessage(new DomainJson(), "items", "may not be empty");
  }

  @Test
  public void validate_fail_whenAboveSixAndUnderSixAreNulls() {
    assertViolationMessage(
        new DomainJson(), "aboveSix", "Either above_six or under_six field should have a value");
  }

  @Test
  public void validate_fail_whenCommentIsLongerThan2500Symbols() {
    final DomainJson testSubject = new DomainJson().setComment(RandomStringUtils.random(2501));
    assertViolationMessage(testSubject, "comment", "size must be between 0 and 2500");
  }

  @Test
  public void validate_fail_whenCaregiverNameIsLongerThan50Symbols() {
    final DomainJson testSubject = new DomainJson().setCaregiverName(RandomStringUtils.random(51));
    assertViolationMessage(testSubject, "caregiverName", "size must be between 0 and 50");
  }

  private void assertViolationMessage(
      final DomainJson testSubject, final String propertyPath, final String violationMessage) {
    final Set<ConstraintViolation<DomainJson>> violations = validator.validate(testSubject);
    final List<ConstraintViolation<DomainJson>> codeViolations =
        violations
            .stream()
            .filter(v -> propertyPath.equals(v.getPropertyPath().toString()))
            .filter(v -> violationMessage.equals(v.getMessage()))
            .collect(Collectors.toList());
    assertThat(codeViolations.size(), is(1));
  }
}
