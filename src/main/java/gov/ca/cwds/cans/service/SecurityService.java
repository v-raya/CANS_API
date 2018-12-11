package gov.ca.cwds.cans.service;

import gov.ca.cwds.security.utils.PrincipalUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityService {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityService.class);

  public boolean isPermitted(String permission) {
    try {
      return SecurityUtils.getSubject().isPermitted(permission);
    } catch (AuthorizationException e) { // NOSONAR
      LOG.debug("user: " + PrincipalUtils.getPrincipal().getStaffId() + " doesn't have permission: "
          + permission, e.getCause());
      return false;
    }
  }

  public void checkPermission(String permission) {
    SecurityUtils.getSubject().checkPermission(permission);
  }
}
