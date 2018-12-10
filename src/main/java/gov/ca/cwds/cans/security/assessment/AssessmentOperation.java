package gov.ca.cwds.cans.security.assessment;

import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import lombok.Getter;

@Getter
public enum AssessmentOperation {
  create(AssessmentCreateAuthorizer.class),
  read(AssessmentReadAuthorizer.class),
  update(AssessmentUpdateAuthorizer.class),
  delete(AssessmentDeleteAuthorizer.class),
  complete(AssessmentCompleteAuthorizer.class);

  private Class<? extends BaseAuthorizer> authorizer;
  private String permission;

  AssessmentOperation(Class<? extends BaseAuthorizer> authorizer) {
    this.authorizer = authorizer;
    this.permission = "assessment:" + toString();
  }

  public String permission(Long id) {
    return permission + ":" + id;
  }
}
