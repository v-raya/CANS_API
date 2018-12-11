package gov.ca.cwds.cans.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;

public class SecurityService {

  public boolean isPermitted(String permission) {
    try {
      return SecurityUtils.getSubject().isPermitted(permission);
    } catch (AuthorizationException e) { // NOSONAR
      return false;
    }
  }

  public void checkPermission(String permission) {
    SecurityUtils.getSubject().checkPermission(permission);
  }
}
