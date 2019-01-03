package gov.ca.cwds.cans.validation;

import gov.ca.cwds.cans.domain.json.DomainJson;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/** @author denys.davydov */
public class ValidDomainJsonValidator implements ConstraintValidator<ValidDomainJson, DomainJson> {

  @Override
  public void initialize(ValidDomainJson constraintAnnotation) {
    // nothing to do here
  }

  @Override
  public boolean isValid(DomainJson domain, ConstraintValidatorContext context) {
    if (domain == null) {
      return true;
    }

    if (domain.getAboveSix() == null && domain.getUnderSix() == null) {
      if (context != null) {
        context
            .buildConstraintViolationWithTemplate(
                "Either above_six or under_six field should have a value")
            .addPropertyNode("aboveSix")
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
      }
      return false;
    }

    return true;
  }
}
