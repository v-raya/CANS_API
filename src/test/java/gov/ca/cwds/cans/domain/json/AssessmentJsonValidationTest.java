package gov.ca.cwds.cans.domain.json;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.Test;

/** @author denys.davydov */
public class AssessmentJsonValidationTest {
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void validate_fail_whenNoDomains() {
    final AssessmentJson assessmentJson = new AssessmentJson();
    final Set<ConstraintViolation<AssessmentJson>> violations = validator.validate(assessmentJson);
    final List<ConstraintViolation<AssessmentJson>> domainsViolations =
        violations
            .stream()
            .filter(v -> "domains".equals(v.getPropertyPath().toString()))
            .collect(Collectors.toList());
    assertThat(domainsViolations.size(), is(1));
    assertThat(domainsViolations.get(0).getMessage(), is("may not be empty"));
  }

  @Test
  public void validate_fail_whenHasInvalidDomain() {
    final AssessmentJson assessmentJson =
        new AssessmentJson().setDomains(singletonList(new DomainJson()));
    final Set<ConstraintViolation<AssessmentJson>> violations = validator.validate(assessmentJson);
    final List<ConstraintViolation<AssessmentJson>> domainsViolations =
        violations
            .stream()
            .filter(v -> "domains[0].aboveSix".equals(v.getPropertyPath().toString()))
            .collect(Collectors.toList());
    assertThat(domainsViolations.size(), is(1));
    assertThat(
        domainsViolations.get(0).getMessage(),
        is("Either above_six or under_six field should have a value"));
  }

  @Test
  public void validate_success_whenValidDomains() {
    final DomainJson validDomain =
        new DomainJson().setCode("code").setAboveSix(true).setItems(singletonList(new ItemJson()));
    final AssessmentJson assessmentJson =
        new AssessmentJson().setDomains(singletonList(validDomain));
    final Set<ConstraintViolation<AssessmentJson>> violations = validator.validate(assessmentJson);
    assertThat(violations.isEmpty(), is(true));
  }
}
