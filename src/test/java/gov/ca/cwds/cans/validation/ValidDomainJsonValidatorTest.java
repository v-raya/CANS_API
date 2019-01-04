package gov.ca.cwds.cans.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.cans.domain.json.DomainJson;
import org.junit.Test;

/** @author denys.davydov */
public class ValidDomainJsonValidatorTest {
  private final ValidDomainJsonValidator testSubject = new ValidDomainJsonValidator();

  @Test
  public void isValid_returnsTrue_whenUnderSixHasValue() {
    final boolean valid = testSubject.isValid(new DomainJson().setUnderSix(false), null);
    assertThat(valid, is(true));
  }

  @Test
  public void isValid_returnsTrue_whenAboveSixHasValue() {
    final boolean valid = testSubject.isValid(new DomainJson().setUnderSix(true), null);
    assertThat(valid, is(true));
  }

  @Test
  public void isValid_returnsFalse_whenAboveSixAndUnderSixAreNulls() {
    final boolean valid = testSubject.isValid(new DomainJson(), null);
    assertThat(valid, is(false));
  }
}
