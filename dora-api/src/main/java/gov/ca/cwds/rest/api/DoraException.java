package gov.ca.cwds.rest.api;

/**
 * @author CWDS TPT-2
 */
public class DoraException extends RuntimeException {

  public DoraException() {
    // default
  }

  public DoraException(String message) {
    super(message);
  }

  public DoraException(Throwable cause) {
    super(cause);
  }

  public DoraException(String message, Throwable cause) {
    super(message, cause);
  }

  public DoraException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
