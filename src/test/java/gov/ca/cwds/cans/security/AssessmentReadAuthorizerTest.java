package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.test.util.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class AssessmentReadAuthorizerTest extends BaseUnitTest {

  @Inject
  private AssessmentReadAuthorizer assessmentReadAuthorizer;

  @Test
  public void checkInstance_authorized_whenUserHasSealedAndClientIsSealed() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Assessment assessment = new Assessment();
    Person person = new Person();
    person.setSensitivityType(SensitivityType.SEALED);
    assessment.setPerson(person);
    Assert.assertTrue(assessmentReadAuthorizer.checkInstance(assessment));
  }

  @Test
  public void checkInstance_authorized_whenUserHasNotSealedAndClientIsNotSealed() throws Exception {
    securityContext("fixtures/perry-account/authorized-no-sealed.json");
    Assessment assessment = new Assessment();
    Person person = new Person();
    assessment.setPerson(person);
    Assert.assertTrue(assessmentReadAuthorizer.checkInstance(assessment));
  }

  @Test
  public void checkInstance_unauthorized_whenUserHasNotSealedAndClientIsSealed() throws Exception {
    securityContext("fixtures/perry-account/authorized-no-sealed.json");
    Assessment assessment = new Assessment();
    Person person = new Person();
    person.setSensitivityType(SensitivityType.SEALED);
    assessment.setPerson(person);
    Assert.assertFalse(assessmentReadAuthorizer.checkInstance(assessment));
  }

  @Test
  public void checkInstance_authorized_whenUserHasSealedAndClientIsNotSealed() throws Exception {
    securityContext("fixtures/perry-account/000-all-authorized.json");
    Assessment assessment = new Assessment();
    Person person = new Person();
    assessment.setPerson(person);
    Assert.assertTrue(assessmentReadAuthorizer.checkInstance(assessment));
  }

}
