package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_INDEX_PREFIX;

import java.util.Map;

import com.codahale.metrics.health.HealthCheck.Result;

import gov.ca.cwds.rest.resources.system.SystemHealthStatusStrategy;

/**
 * SystemHealthStatusStrategy with its method returning true for System Health Status, even if there
 * is an unhealthy ES index. It will return false, if something critical is not working, for
 * example, ES is down or a required ES plugin is not installed.
 */
public class DoraSystemHealthStatusStrategy implements SystemHealthStatusStrategy {

  @Override
  public boolean getSystemHealthStatus(Map<String, Result> healthChecks) {
    return healthChecks.entrySet().stream().filter(e -> !e.getKey().startsWith(HC_INDEX_PREFIX))
        .allMatch(e -> e.getValue().isHealthy());
  }

}
