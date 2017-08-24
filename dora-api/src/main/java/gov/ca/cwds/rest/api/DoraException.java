package gov.ca.cwds.rest.api;

/**
 * @author CWDS TPT-2
 */
public class DoraException extends RuntimeException {

  public DoraException(String message) {
    super(message);
  }

  public DoraException(String message, Throwable cause) {
    super(message, cause);
  }
}
