package gov.ca.cwds.cans.security;

import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.security.assessment.AssessmentOperation;
import gov.ca.cwds.cans.security.assessment.facts.AssessmentOperationFact;
import gov.ca.cwds.cans.service.CansRulesService;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.test.support.TestPrincipalUtils;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Assert;
import org.junit.Test;

public class AccessDecisionTableTest {

  @Test
  public void testDecisionTable() {
    printCompiledDTable();
    final PerryAccount perryAccount = new PerryAccount();
    perryAccount.setPrivileges(new HashSet<>(Collections.singletonList("CANS-assessment-read")));
    perryAccount.setCountyCwsCode("1111");
    TestPrincipalUtils.login(perryAccount);
    CansRulesService droolsService = new CansRulesService();
    Assessment assessment = new Assessment();
    assessment.setCounty(
        new County() {
          {
            setExternalId("1111");
          }
        });
    AssessmentOperationFact fact =
        new AssessmentOperationFact(AssessmentOperation.read, assessment, perryAccount, true);
    Assert.assertTrue(droolsService.authorize(fact, assessment));
    fact = new AssessmentOperationFact(AssessmentOperation.create, assessment, perryAccount, true);
    Assert.assertFalse(droolsService.authorize(fact, assessment));
    fact = new AssessmentOperationFact(AssessmentOperation.read, assessment, perryAccount, false);
    Assert.assertFalse(droolsService.authorize(fact, assessment));
  }

  private void printCompiledDTable() {
    InputStream is =
        getClass()
            .getClassLoader()
            .getResourceAsStream("authorization-rules/access-decision-table.xlsx");

    SpreadsheetCompiler sc = new SpreadsheetCompiler();
    String drl = sc.compile(is, InputType.XLS);
    System.out.println(drl);
  }
}
