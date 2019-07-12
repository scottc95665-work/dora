package gov.ca.cwds.rest;

/**
 * Constants defining resources locations in the API.
 *
 * @author CDWS API Team
 */
public final class DoraConstants {

  public static final String SYSTEM_INFORMATION = "system-information";
  public static final String RESOURCE_ELASTICSEARCH_INDEX_QUERY = "dora";
  public static final String PROD_MODE = "PROD";
  public static final String DEV_MODE = "DEV";

  private DoraConstants() {
    // default
  }

  public static class Index {

    public static final String FACILITIES_INDEX = "facilities";
    public static final String PEOPLE_SUMMARY_INDEX = "people-summary";

    private Index() {
      // default
    }
  }

  public static class Plugin {

    public static final String X_PACK_PLUGIN = "perry_realm";
    public static final String PHONETIC_PLUGIN = "analysis-phonetic";

    private Plugin() {
      // default
    }
  }

  public static class Role {

    public static final String WORKER_ROLE = "worker";
    public static final String PEOPLE_WORKER_ROLE = "people_worker";
    public static final String PEOPLE_SENSITIVE_ROLE = "people_sensitive";
    public static final String PEOPLE_SENSITIVE_NO_COUNTY_ROLE = "people_sensitive_no_county";
    public static final String PEOPLE_SEALED_ROLE = "people_sealed";
    public static final String PEOPLE_SEALED_NO_COUNTY_ROLE = "people_sealed_no_county";
    public static final String PEOPLE_SUMMARY_WORKER_ROLE = "people_summary_worker";

    private Role() {
      // default
    }
  }

  public static class HealthCheck {

    public static final String HC_DEADLOCKS = "deadlocks";
    public static final String HC_ES_CONFIG = "dora-es-config";
    public static final String HC_ES_STATUS = "elasticsearch-status";

    public static final String HC_INDEX_PREFIX = "elasticsearch-index-";
    public static final String HC_FACILITIES_INDEX = HC_INDEX_PREFIX + Index.FACILITIES_INDEX;
    public static final String HC_PEOPLE_SUMMARY_INDEX =
        HC_INDEX_PREFIX + Index.PEOPLE_SUMMARY_INDEX;

    public static final String HC_PLUGIN_PREFIX = "elasticsearch-plugin-";
    public static final String HC_X_PACK_PLUGIN = HC_PLUGIN_PREFIX + Plugin.X_PACK_PLUGIN;
    public static final String HC_PHONETIC_PLUGIN = HC_PLUGIN_PREFIX + Plugin.PHONETIC_PLUGIN;

    public static final String HC_ROLE_PREFIX = "elasticsearch-role-";
    public static final String HC_WORKER_ROLE = HC_ROLE_PREFIX + Role.WORKER_ROLE;
    public static final String HC_PEOPLE_WORKER_ROLE = HC_ROLE_PREFIX + Role.PEOPLE_WORKER_ROLE;
    public static final String HC_PEOPLE_SENSITIVE_ROLE =
        HC_ROLE_PREFIX + Role.PEOPLE_SENSITIVE_ROLE;
    public static final String HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE =
        HC_ROLE_PREFIX + Role.PEOPLE_SENSITIVE_NO_COUNTY_ROLE;
    public static final String HC_PEOPLE_SEALED_ROLE = HC_ROLE_PREFIX + Role.PEOPLE_SEALED_ROLE;
    public static final String HC_PEOPLE_SEALED_NO_COUNTY_ROLE =
        HC_ROLE_PREFIX + Role.PEOPLE_SEALED_NO_COUNTY_ROLE;
    public static final String HC_PEOPLE_SUMMARY_WORKER_ROLE =
        HC_ROLE_PREFIX + Role.PEOPLE_SUMMARY_WORKER_ROLE;

    private HealthCheck() {
      // default
    }
  }
}
