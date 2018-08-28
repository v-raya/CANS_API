package gov.ca.cwds.cans.rest.auth;

import gov.ca.cwds.security.authorizer.StaticAuthorizer;
import gov.ca.cwds.security.realm.PerryAccount;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;

public class CansStaticAuthorizer implements StaticAuthorizer {

  public static final String CANS_ROLLOUT_PERMISSION = "CANS-rollout";
  public static final String CANS_WORKER_ROLE = "CANS-worker";

  /**
   * Implementation of StaticAuthorizer.
   * @param perryAccount Perry Account
   * @param simpleAuthInfo Simple Authorization information
   */
  @Override
  public void authorize(PerryAccount perryAccount, SimpleAuthorizationInfo simpleAuthInfo) {
    if (perryAccount.getPrivileges().stream()
        .anyMatch(CANS_ROLLOUT_PERMISSION::equals)) {
      simpleAuthInfo.addObjectPermission(new WildcardPermission(CANS_ROLLOUT_PERMISSION));
    }
  }

}
