package gov.ca.cwds.rest;

import javax.ws.rs.client.Entity;

/**
 * @author TPT-2
 */
public final class SmokeTestUtils {

  public static final String DORA_URL_PROP = "dora.url";
  public static final String AUTH_MODE_PROP = "auth.mode";
  public static final String PERRY_URL_PROP = "perry.url";
  public static final String SMOKE_TEST_USER_ENV = "SMOKE_TEST_USER";
  public static final String SMOKE_TEST_PASSWORD_ENV = "SMOKE_TEST_PASSWORD";
  public static final String SMOKE_VERIFICATION_CODE_ENV = "SMOKE_VERIFICATION_CODE";

  private static final String DEV_AUTH_MODE = "dev";
  private static final String INTEGRATION_AUTH_MODE = "integration";

  public static final Entity BLANK_JSON_ENTITY = Entity.json("{}");

  private SmokeTestUtils() {
    // utility class
  }

  public static boolean isDevAuthMode() {
    return DEV_AUTH_MODE.equals(System.getProperty(AUTH_MODE_PROP));
  }

  public static boolean isIntegrationAuthMode() {
    return INTEGRATION_AUTH_MODE.equals(System.getProperty(AUTH_MODE_PROP));
  }
}
