package gov.ca.cwds.dora.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration.XpackConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT2 Team
 */
public class BasicDoraHealthCheck extends HealthCheck {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicDoraHealthCheck.class);

  private static final String HEALTHY_ES_CONFIG_MSG = "Dora is configured for Elasticsearch on %s:%s with X-pack %s.";
  private static final String UNHEALTHY_ES_CONFIG_MSG = "Dora is not properly configured for Elasticsearch.";

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
    if (elasticsearchConfigurationIsHealthy()) {
      String xPackStatus = esConfig.getXpack().isEnabled() ? "enabled" : "disabled";
      String healthyMsg = String.format(HEALTHY_ES_CONFIG_MSG, esConfig.getHost(), esConfig.getPort(), xPackStatus);
      LOGGER.info(healthyMsg);
      return Result.healthy(healthyMsg);
    } else {
      LOGGER.error(HEALTHY_ES_CONFIG_MSG);
      return HealthCheck.Result.unhealthy(UNHEALTHY_ES_CONFIG_MSG);
    }
  }

  private boolean elasticsearchConfigurationIsHealthy() {
    return esConfig.getHost() != null && esConfig.getPort() != null
        && xpackConfigurationIsHealthy(esConfig.getXpack());
  }

  private boolean xpackConfigurationIsHealthy(XpackConfiguration xpackConfig) {
    return xpackConfig != null && (!xpackConfig.isEnabled()
        || xpackConfig.getUser() != null && xpackConfig.getPassword() != null);
  }
}
