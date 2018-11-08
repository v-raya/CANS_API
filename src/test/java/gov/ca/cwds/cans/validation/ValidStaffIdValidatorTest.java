package gov.ca.cwds.cans.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/** @author denys.davydov */
public class ValidStaffIdValidatorTest {

  private ValidStaffIdValidator testSubject = new ValidStaffIdValidator();

  @Test
  public void isValid_returnsTrue_whenValidInput() {
    assertThat(testSubject.isValid("0X5", null), is(true));
    assertThat(testSubject.isValid("123", null), is(true));
    assertThat(testSubject.isValid("zzz", null), is(true));
  }

  @Test
  public void isValid_returnsTrue_whenInputIsNull() {
    assertThat(testSubject.isValid(null, null), is(true));
  }

  @Test
  public void isValid_returnsFalse_whenValidInput() {
    assertThat(testSubject.isValid("", null), is(false));
    assertThat(testSubject.isValid("a", null), is(false));
    assertThat(testSubject.isValid("1Zc4", null), is(false));
  }
}
