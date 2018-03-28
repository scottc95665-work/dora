package gov.ca.cwds.dora.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration.XpackConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public class BasicDoraHealthCheck extends HealthCheck {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicDoraHealthCheck.class);

  static final String HEALTHY_ES_CONFIG_MSG = "Dora is configured for Elasticsearch on nodes %s with X-pack %s.";
  static final String UNHEALTHY_ES_CONFIG_MSG = "Dora is not properly configured for Elasticsearch.";

  ElasticsearchConfiguration esConfig;

  /**
   * Constructor
   *
   * @param esConfig instance of ElasticsearchConfiguration
   */
  @Inject
  public BasicDoraHealthCheck(ElasticsearchConfiguration esConfig) {
    this.esConfig = esConfig;
  }

  @Override
  protected Result check() throws Exception {

    if (elasticsearchConfigurationIsHealthy(esConfig.getNodes())) {
      String xPackStatus = esConfig.getXpack().isEnabled() ? "enabled" : "disabled";
      String healthyMsg = String.format(HEALTHY_ES_CONFIG_MSG, esConfig.getNodes(), xPackStatus);
      LOGGER.info(healthyMsg);
      return Result.healthy(healthyMsg);
    } else {
      LOGGER.error(UNHEALTHY_ES_CONFIG_MSG);
      return HealthCheck.Result.unhealthy(UNHEALTHY_ES_CONFIG_MSG);
    }
  }

  private boolean elasticsearchConfigurationIsHealthy(String nodes) {
    HttpHost[] httpHosts = DoraUtils.parseNodes(nodes);
    if (httpHosts == null) {
      return false;
    }
    for (HttpHost httpHost : httpHosts) {
      if (StringUtils.isBlank(httpHost.getHostName()) || httpHost.getPort() == -1) {
        return false;
      }
    }
    return !StringUtils.isAnyBlank(esConfig.getUser(), esConfig.getPassword());
  }
}
