package gov.ca.cwds.cans.validation;

import static gov.ca.cwds.cans.domain.enumeration.RatingType.NOT_APPLICABLE_RATING_VALUE;

import gov.ca.cwds.cans.domain.enumeration.RatingType;
import gov.ca.cwds.cans.domain.json.ItemJson;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/** @author denys.davydov */
public class ValidItemJsonValidator implements ConstraintValidator<ValidItemJson, ItemJson> {

  @Override
  public void initialize(ValidItemJson constraintAnnotation) {
    // nothing to do here
  }

  @Override
  public boolean isValid(ItemJson item, ConstraintValidatorContext context) {
    if (item == null) {
      return true;
    }
    return validateIds(item, context) & validateRating(item, context);
  }

  private boolean validateIds(ItemJson item, ConstraintValidatorContext context) {
    if (item.getAboveSixId() == null && item.getUnderSixId() == null) {
      if (context != null) {
        context
            .buildConstraintViolationWithTemplate(
                "Either above_six_id or under_six_id field should have a value")
            .addPropertyNode("aboveSixId")
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
      }
      return false;
    }
    return true;
  }

  private boolean validateRating(ItemJson item, ConstraintValidatorContext context) {
    if (!hasPermittedNaRating(item)) {
      final RatingType ratingType = item.getRatingType();
      final Integer rating = item.getRating();
      if (ratingType != null && !ratingType.isValidRating(rating)) {
        if (context != null) {
          context
              .buildConstraintViolationWithTemplate(
                  String.format(
                      "Item with rating_type = [%s] with has_na_option = [%s] can't have a rating = [%s]",
                      ratingType, item.getHasNaOption(), rating))
              .addPropertyNode("rating")
              .addConstraintViolation()
              .disableDefaultConstraintViolation();
        }
        return false;
      }
    }
    return true;
  }

  private boolean hasPermittedNaRating(ItemJson item) {
    return Boolean.TRUE == item.getHasNaOption()
        && NOT_APPLICABLE_RATING_VALUE.equals(item.getRating());
  }
}
