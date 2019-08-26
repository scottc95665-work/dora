package gov.ca.cwds.rest.filters;

/**
 * Request execution context registry based on ThredLocal
 * 
 * @author CWDS API Team
 */
public class RequestExecutionContextRegistry {

  private static final ThreadLocal<RequestExecutionContext> pegged = new ThreadLocal<>();

  private RequestExecutionContextRegistry() {}

  /**
   * Register RequestExecutionContext with ThreadLocal
   * 
   * @param requestExecutionContext
   */
  static void register(RequestExecutionContext requestExecutionContext) {
    pegged.set(requestExecutionContext);
  }

  /**
   * Remove RequestExecutionContext from ThreadLocal
   */
  static void remove() {
    pegged.remove();
  }

  /**
   * Get RequestExecutionContext from ThreadLocal
   */
  static RequestExecutionContext get() {
    return pegged.get();
  }

}
