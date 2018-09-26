package gov.ca.cwds.cans.exception;

import gov.ca.cwds.rest.exception.ExpectedException;
import javax.ws.rs.core.Response.Status;

public class DuplicationException extends ExpectedException {

  private static final long serialVersionUID = -1522347575617447850L;

  public DuplicationException(String message) {
    super(message, Status.CONFLICT);
  }
}
