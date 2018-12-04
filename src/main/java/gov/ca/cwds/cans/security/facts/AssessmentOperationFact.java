package gov.ca.cwds.cans.security.facts;

import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import lombok.Getter;

@Getter
public class AssessmentOperationFact {

  private String operation;
  private PerryAccount user;
  private Assessment assessment;
  private boolean sameCounty;
  private boolean hasAccess;

  public AssessmentOperationFact(String operation, Assessment assessment, boolean hasAccess) {
    this.operation = operation;
    this.assessment = assessment;
    this.user = PrincipalUtils.getPrincipal();
    this.sameCounty = user.getCountyCwsCode().equals(assessment.getCounty().getExternalId());
    this.hasAccess = hasAccess;
  }
}
