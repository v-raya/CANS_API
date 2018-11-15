package gov.ca.cwds.cans.validation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** @author denys.davydov */
@Target({PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidStaffIdValidator.class)
@Documented
public @interface ValidStaffId {

  String message() default "StaffId is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
