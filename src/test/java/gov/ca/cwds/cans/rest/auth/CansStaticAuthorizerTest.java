package gov.ca.cwds.cans.rest.auth;

import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.AUTHORIZED_ACCOUNT_FIXTURE;
import static gov.ca.cwds.cans.rest.resource.AbstractFunctionalTest.NOT_AUTHORIZED_ACCOUNT_FIXTURE;
import static gov.ca.cwds.cans.test.util.FixtureReader.readObject;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

import gov.ca.cwds.security.authorizer.StaticAuthorizer;
import gov.ca.cwds.security.realm.PerryAccount;
import java.io.IOException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;

public class CansStaticAuthorizerTest {

  public CansStaticAuthorizerTest() {
  }

  private StaticAuthorizer cansStaticAuthorizer = new CansStaticAuthorizer();

  @Test
  public void testAuthorize_whenUserAuthorized() throws IOException {
    PerryAccount authorizedUserAccount = readObject(AUTHORIZED_ACCOUNT_FIXTURE, PerryAccount.class);
    SimpleAuthorizationInfo simpleAuthInfo = new SimpleAuthorizationInfo();
    cansStaticAuthorizer.authorize(authorizedUserAccount, simpleAuthInfo);
    Permission cansRolloutPermission = new WildcardPermission(CANS_ROLLOUT_PERMISSION);
    assertThat(simpleAuthInfo.getObjectPermissions(), hasItems(cansRolloutPermission));
  }

  @Test
  public void testAuthorize_whenUserHasNoRoleAndPrivilege() throws IOException {
    PerryAccount userAccount = readObject(NOT_AUTHORIZED_ACCOUNT_FIXTURE, PerryAccount.class);
    SimpleAuthorizationInfo simpleAuthInfo = new SimpleAuthorizationInfo();
    cansStaticAuthorizer.authorize(userAccount, simpleAuthInfo);
    assertTrue(simpleAuthInfo.getObjectPermissions() == null);
  }
}
