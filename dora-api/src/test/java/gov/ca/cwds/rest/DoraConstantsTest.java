package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.DEV_MODE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_DEADLOCKS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_CONFIG;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_STATUS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_INDEX_PREFIX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SEALED_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SEALED_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SENSITIVE_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PLUGIN_PREFIX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ROLE_PREFIX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.PROD_MODE;
import static gov.ca.cwds.rest.DoraConstants.Plugin.PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Plugin.X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SEALED_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SEALED_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SENSITIVE_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SENSITIVE_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SUMMARY_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class DoraConstantsTest {

  @Test
  public void testConstants() {
    Assert.assertEquals("system-information", SYSTEM_INFORMATION);
    Assert.assertEquals("dora", RESOURCE_ELASTICSEARCH_INDEX_QUERY);
    Assert.assertEquals("PROD", PROD_MODE);
    Assert.assertEquals("DEV", DEV_MODE);

    Assert.assertEquals("facilities", FACILITIES_INDEX);
    Assert.assertEquals("people-summary", PEOPLE_SUMMARY_INDEX);

    Assert.assertEquals("x-pack", X_PACK_PLUGIN);
    Assert.assertEquals("analysis-phonetic", PHONETIC_PLUGIN);

    Assert.assertEquals("worker", WORKER_ROLE);
    Assert.assertEquals("people_worker", PEOPLE_WORKER_ROLE);
    Assert.assertEquals("people_sensitive", PEOPLE_SENSITIVE_ROLE);
    Assert.assertEquals("people_sensitive_no_county", PEOPLE_SENSITIVE_NO_COUNTY_ROLE);
    Assert.assertEquals("people_sealed", PEOPLE_SEALED_ROLE);
    Assert.assertEquals("people_sealed_no_county", PEOPLE_SEALED_NO_COUNTY_ROLE);
    Assert.assertEquals("people_summary_worker", PEOPLE_SUMMARY_WORKER_ROLE);

    Assert.assertEquals("deadlocks", HC_DEADLOCKS);
    Assert.assertEquals("dora-es-config", HC_ES_CONFIG);
    Assert.assertEquals("elasticsearch-status", HC_ES_STATUS);
    Assert.assertEquals("elasticsearch-index-", HC_INDEX_PREFIX);
    Assert.assertEquals("elasticsearch-index-facilities", HC_FACILITIES_INDEX);
    Assert.assertEquals("elasticsearch-index-people-summary", HC_PEOPLE_SUMMARY_INDEX);
    Assert.assertEquals("elasticsearch-plugin-", HC_PLUGIN_PREFIX);
    Assert.assertEquals("elasticsearch-plugin-x-pack", HC_X_PACK_PLUGIN);
    Assert.assertEquals("elasticsearch-plugin-analysis-phonetic", HC_PHONETIC_PLUGIN);
    Assert.assertEquals("elasticsearch-role-", HC_ROLE_PREFIX);
    Assert.assertEquals("elasticsearch-role-worker", HC_WORKER_ROLE);
    Assert.assertEquals("elasticsearch-role-people_worker", HC_PEOPLE_WORKER_ROLE);
    Assert.assertEquals("elasticsearch-role-people_sensitive", HC_PEOPLE_SENSITIVE_ROLE);
    Assert.assertEquals("elasticsearch-role-people_sensitive_no_county",
        HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE);
    Assert.assertEquals("elasticsearch-role-people_sealed", HC_PEOPLE_SEALED_ROLE);
    Assert.assertEquals("elasticsearch-role-people_sealed_no_county",
        HC_PEOPLE_SEALED_NO_COUNTY_ROLE);
    Assert.assertEquals("elasticsearch-role-people_summary_worker", HC_PEOPLE_SUMMARY_WORKER_ROLE);
  }
}
