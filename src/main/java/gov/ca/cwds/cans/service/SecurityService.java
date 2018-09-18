package gov.ca.cwds.cans.service;

import org.apache.shiro.SecurityUtils;

public class SecurityService {

  public Boolean checkPermission(String permission) {
    try {
      SecurityUtils.getSubject().checkPermission(permission);
    } catch (Exception e) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

}
