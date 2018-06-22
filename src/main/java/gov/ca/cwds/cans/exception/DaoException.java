package gov.ca.cwds.cans.exception;

/** @author denys.davydov */
public class DaoException extends RuntimeException {

  private static final long serialVersionUID = -1522328644617447850L;

  public DaoException() {}

  public DaoException(String message, Throwable cause) {
    super(message, cause);
  }

}
