package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.test.util.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class AssessmentWriteAuthorizerTest extends BaseUnitTest {

  @Inject
  private AssessmentWriteAuthorizer assessmentWriteAuthorizer;

  @Test
  public void testAuthorized() throws Exception {
    securityContext("fixtures/perry-account/el-dorado-all-authorized.json");
    Assessment assessment = new Assessment();
    County county = new County();
    county.setName("El Dorado");
    assessment.setCounty(county);
    Assert.assertTrue(assessmentWriteAuthorizer.checkInstance(assessment));
  }

  @Test
  public void testUnauthorized() throws Exception {
    securityContext("fixtures/perry-account/el-dorado-all-authorized.json");
    Assessment assessment = new Assessment();
    County county = new County();
    county.setName("Sacramento");
    assessment.setCounty(county);
    Assert.assertFalse(assessmentWriteAuthorizer.checkInstance(assessment));
  }

}
