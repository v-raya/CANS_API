package gov.ca.cwds.cans.validation;

import gov.ca.cwds.cans.domain.dto.person.PersonShortDto;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author dmitry.rudenko
 */
public class ValidPersonValidator implements ConstraintValidator<ValidPerson, PersonShortDto> {


  @Override
  public void initialize(ValidPerson constraintAnnotation) {

  }

  @Override
  public boolean isValid(PersonShortDto value, ConstraintValidatorContext context) {
    return dobMustNotBeFutureDate(value.getDob(), context);
  }

  private boolean dobMustNotBeFutureDate(LocalDate dob, ConstraintValidatorContext context) {
    if(dob!= null && (dob.compareTo(LocalDate.now()) >= 0)) {
      context
          .buildConstraintViolationWithTemplate("Date of birth must not be a future date")
          .addPropertyNode("dob")
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
      return false;
    }
    return true;
  }
}
