package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.test.util.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class PersonReadAuthorizerTest extends BaseUnitTest {

  @Inject
  private PersonReadAuthorizer personReadAuthorizer;


  @Test
  public void checkInstance_authorized_whenUserHasSealedAndClientHasNoCounty() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = getPerson("1088");
    person.setSensitivityType(SensitivityType.SEALED);
    person.setCounty(null);
    Assert.assertTrue(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_authorized_whenUserHasSealedAndClientIsSealed() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = getPerson("1088");
    person.setSensitivityType(SensitivityType.SEALED);
    Assert.assertTrue(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_authorized_whenUserHasSealedAndClientIsSealedButDifferentCounty()
      throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = getPerson("0000");
    person.setSensitivityType(SensitivityType.SEALED);
    Assert.assertFalse(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_authorized_whenUserHasNotSealedAndClientIsNotSealed() throws Exception {
    securityContext("fixtures/perry-account/authorized-no-sealed.json");
    Person person = getPerson("1088");
    Assert.assertTrue(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_unauthorized_whenUserHasNotSealedAndClientIsSealed() throws Exception {
    securityContext("fixtures/perry-account/authorized-no-sealed.json");
    Person person = getPerson("1088");
    person.setSensitivityType(SensitivityType.SEALED);
    Assert.assertFalse(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_authorized_whenUserHasSealedAndClientIsNotSealed() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = getPerson("1088");
    Assert.assertTrue(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_unauthorized_whenUserHasNotSensitiveAndClientIsSensitive() throws Exception {
    securityContext("fixtures/perry-account/no_sealed_no_sensitive-authorized.json");
    Person person = new Person();
    person.setSensitivityType(SensitivityType.SENSITIVE);
    Assert.assertFalse(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_authorized_whenUserHasSensitiveAndClientHasNoCounty() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = getPerson("1088");
    person.setSensitivityType(SensitivityType.SENSITIVE);
    person.setCounty(null);
    Assert.assertTrue(personReadAuthorizer.checkInstance(person));
  }

  @Test
  public void checkInstance_unauthorized_whenUserHasOtherCountyThanPerson() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Person person = new Person();

    County elDoradoCounty = new County();
    elDoradoCounty.setId(9L);
    elDoradoCounty.setName("El Dorado");
    elDoradoCounty.setExportId("09");
    elDoradoCounty.setExternalId("1076");

    person.setCounty(elDoradoCounty);
    person.setSensitivityType(SensitivityType.SENSITIVE);
    Assert.assertFalse(personReadAuthorizer.checkInstance(person));
  }

  static Person getPerson(String countyExtId) {
    Person person = new Person();
    County county = new County();
    county.setExternalId(countyExtId);
    person.setCounty(county);
    return person;
  }

}
