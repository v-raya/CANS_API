package gov.ca.cwds.cans.rest.auth;

import gov.ca.cwds.security.authorizer.StaticAuthorizer;
import gov.ca.cwds.security.realm.PerryAccount;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;

public class CansStaticAuthorizer implements StaticAuthorizer {

  public static final String CANS_ROLLOUT_PERMISSION = "CANS-rollout";

  /**
   * Implementation of StaticAuthorizer.
   *
   * @param perryAccount Perry Account
   * @param simpleAuthInfo Simple Authorization information
   */
  @Override
  public void authorize(PerryAccount perryAccount, SimpleAuthorizationInfo simpleAuthInfo) {
    perryAccount
        .getPrivileges()
        .forEach(
            privilage -> simpleAuthInfo.addObjectPermission(new WildcardPermission(privilage)));
  }
}
