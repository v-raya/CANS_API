package gov.ca.cwds.cans.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/** @author denys.davydov */
public class ValidStaffIdValidator implements ConstraintValidator<ValidStaffId, String> {

  private static final String PATTERN = "^[A-Za-z0-9]{3}$";

  @Override
  public void initialize(ValidStaffId constraintAnnotation) {
    // nothing to do
  }

  @Override
  public boolean isValid(String staffId, ConstraintValidatorContext context) {
    if (staffId == null) {
      return true;
    }
    return staffId.matches(PATTERN);
  }
}
