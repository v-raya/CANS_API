package gov.ca.cwds.cans.util;

/** @author CWDS TPT-2 Team */
public class UpgradeDbException extends RuntimeException {
  private static final long serialVersionUID = -4182482433409192016L;

  public UpgradeDbException(String message) {
    super(message);
  }

  public UpgradeDbException(String message, Throwable cause) {
    super(message, cause);
  }
}
