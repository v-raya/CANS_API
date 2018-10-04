package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.test.util.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class PersonCreateAuthorizerTest extends BaseUnitTest {

  @Inject private PersonCreateAuthorizer personCreateAuthorizer;

  @Test
  public void checkInstance_Unauthorized_whenPersonIsSensitiveAndUserHasDifferentCounty()
      throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = PersonHelper.getPerson("1089");
    person.setSensitivityType(SensitivityType.SENSITIVE);
    Assert.assertFalse(personCreateAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_Unauthorized_whenPersonIsSealedAndUserHasDifferentCounty()
      throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = PersonHelper.getPerson("1089");
    person.setSensitivityType(SensitivityType.SEALED);
    Assert.assertFalse(personCreateAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_Authorized_whenPersonIsSealedAndUserHasSameCounty() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = PersonHelper.getPerson("1088");
    person.setSensitivityType(SensitivityType.SEALED);
    Assert.assertTrue(personCreateAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_Authorized_whenPersonIsSensitiveAndUserHasSameCounty()
      throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = PersonHelper.getPerson("1088");
    person.setSensitivityType(SensitivityType.SENSITIVE);
    Assert.assertTrue(personCreateAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_Authorized_whenPersonHasNoSensitivityType() throws Exception {
    securityContext("fixtures/perry-account/no_sealed_no_sensitive-authorized.json");
    Person person = PersonHelper.getPerson("1089");
    person.setSensitivityType(null);
    Assert.assertTrue(personCreateAuthorizer.checkInstance(person));
  }
}
