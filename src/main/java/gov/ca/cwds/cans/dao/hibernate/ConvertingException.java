package gov.ca.cwds.cans.dao.hibernate;

/**
 * @author CWDS CALS API Team
 */
public class ConvertingException extends RuntimeException {

  public ConvertingException(String s, Exception ex) {
    super(s, ex);
  }
}
