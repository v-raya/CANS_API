package gov.ca.cwds.cans.rest.auth;

import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

import gov.ca.cwds.security.authorizer.StaticAuthorizer;
import gov.ca.cwds.security.realm.PerryAccount;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;

public class CansStaticAuthorizerTest {

  public CansStaticAuthorizerTest() {}

  private StaticAuthorizer cansStaticAuthorizer = new CansStaticAuthorizer();

  @Test
  public void testAuthorize_whenUserAuthorized() {
    Set<String> privileges = new HashSet<>(Arrays.asList("CANS-rollout"));
    PerryAccount authorizedUserAccount = new PerryAccount();
    authorizedUserAccount.setPrivileges(privileges);
    SimpleAuthorizationInfo simpleAuthInfo = new SimpleAuthorizationInfo();
    cansStaticAuthorizer.authorize(authorizedUserAccount, simpleAuthInfo);
    Permission cansRolloutPermission = new WildcardPermission(CANS_ROLLOUT_PERMISSION);
    assertThat(simpleAuthInfo.getObjectPermissions(), hasItems(cansRolloutPermission));
  }

  @Test
  public void testAuthorize_whenUserHasNoRoleAndPrivilege() {
    Set<String> noPrivileges = new HashSet<>();
    PerryAccount userAccount = new PerryAccount();
    userAccount.setPrivileges(noPrivileges);
    SimpleAuthorizationInfo simpleAuthInfo = new SimpleAuthorizationInfo();
    cansStaticAuthorizer.authorize(userAccount, simpleAuthInfo);
    assertTrue(simpleAuthInfo.getObjectPermissions() == null);
  }
}
