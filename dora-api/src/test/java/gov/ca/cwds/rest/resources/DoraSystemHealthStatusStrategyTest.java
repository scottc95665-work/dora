package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_DEADLOCKS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_CONFIG;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_STATUS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_X_PACK_PLUGIN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.codahale.metrics.health.HealthCheck.Result;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class DoraSystemHealthStatusStrategyTest {

  private DoraSystemHealthStatusStrategy healthStatusStrategy = new DoraSystemHealthStatusStrategy();

  @Test
  public void testSystemHealthStatusFalseWhenDeadlocks() {
    Map<String, Result> healthChecks = prepareHealthChecks();
    healthChecks.put(HC_DEADLOCKS, Result.unhealthy(""));
    assertFalse(healthStatusStrategy.getSystemHealthStatus(healthChecks));
  }

  @Test
  public void testSystemHealthStatusTrueWhenIndexNotHealthy() {
    Map<String, Result> healthChecks = prepareHealthChecks();
    healthChecks.put(HC_FACILITIES_INDEX, Result.unhealthy(""));
    assertTrue(healthStatusStrategy.getSystemHealthStatus(healthChecks));
  }

  @Test
  public void testSystemHealthStatusFalseWhenElasticsearchDown() {
    Map<String, Result> healthChecks = prepareHealthChecks();
    healthChecks.put(HC_ES_STATUS, Result.unhealthy(""));
    assertFalse(healthStatusStrategy.getSystemHealthStatus(healthChecks));
  }

  @Test
  public void testSystemHealthStatusFalseWhenNoPlugin() {
    Map<String, Result> healthChecks = prepareHealthChecks();
    healthChecks.put(HC_X_PACK_PLUGIN, Result.unhealthy(""));
    assertFalse(healthStatusStrategy.getSystemHealthStatus(healthChecks));
  }

  private Map<String, Result> prepareHealthChecks() {
    Map<String, Result> healthChecks = new HashMap<>();
    healthChecks.put(HC_DEADLOCKS, Result.healthy());
    healthChecks.put(HC_ES_CONFIG, Result.healthy());
    healthChecks.put(HC_ES_STATUS, Result.healthy());
    healthChecks.put(HC_X_PACK_PLUGIN, Result.healthy());
    healthChecks.put(HC_PHONETIC_PLUGIN, Result.healthy());
    healthChecks.put(HC_PEOPLE_SUMMARY_INDEX, Result.healthy());
    healthChecks.put(HC_FACILITIES_INDEX, Result.healthy());
    return healthChecks;
  }
}
