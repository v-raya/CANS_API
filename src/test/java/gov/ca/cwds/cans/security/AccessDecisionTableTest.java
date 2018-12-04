package gov.ca.cwds.cans.security;

import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.security.facts.AssessmentOperationFact;
import gov.ca.cwds.cans.service.CansDroolsService;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.test.TestSecurityFilter;
import gov.ca.cwds.test.support.TestPrincipalUtils;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Assert;
import org.junit.Test;

public class AccessDecisionTableTest {

  @Test
  public void testDecisionTable() {
    final PerryAccount perryAccount = new PerryAccount();
    perryAccount.setPrivileges(new HashSet<>(Collections.singletonList("CANS-assessment-read")));
    perryAccount.setCountyCwsCode("1111");
    TestPrincipalUtils.login(perryAccount);
    CansDroolsService droolsService = new CansDroolsService();
    Assessment assessment = new Assessment();
    assessment.setCounty(
        new County() {
          {
            setExternalId("1111");
          }
        });
    AssessmentOperationFact fact = new AssessmentOperationFact("Read", assessment, true);
    Assert.assertTrue(droolsService.authorize(fact));
    fact = new AssessmentOperationFact("Create", assessment, true);
    Assert.assertFalse(droolsService.authorize(fact));
    fact = new AssessmentOperationFact("Read", assessment, false);
    Assert.assertFalse(droolsService.authorize(fact));
  }
}
