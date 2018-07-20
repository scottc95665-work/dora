package gov.ca.cwds.dora.health;

import com.google.inject.Inject;
import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.EsRestClientManager;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public class ElasticsearchPluginHealthCheck extends ElasticsearchHealthCheck {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ElasticsearchPluginHealthCheck.class);

  static final String ES_PLUGINS_ENDPOINT = "/_nodes/plugins";

  static final String HEALTHY_ES_PLUGIN_MSG = "%s plugin is installed on every Elasticsearch node.";
  static final String UNHEALTHY_ES_PLUGIN_MSG = "%s plugin is not installed on the following Elasticsearch nodes: %s";

  private String pluginName;

  @Inject
  private EsRestClientManager esRestClientManager;

  /**
   * Constructor
   *
   * @param esConfig instance of ElasticsearchConfiguration
   */
  @Inject
  public ElasticsearchPluginHealthCheck(ElasticsearchConfiguration esConfig, String pluginName) {
    super(esConfig);
    this.pluginName = pluginName;
  }

  @Override
  protected Result check() throws Exception {
    Result result = super.check();
    if (!result.isHealthy()) {
      return result;
    }

    try {
      RestClient esRestClient = esRestClientManager.getEsRestClient();
      Map<String, Object> jsonMap = performRequest(esRestClient, "GET", ES_PLUGINS_ENDPOINT);

      Optional<String> badEsNodesOptional = DoraUtils.extractNodesWithoutPlugin(jsonMap, pluginName)
          .reduce((s1, s2) -> String.join(", ", s1, s2));

      if (badEsNodesOptional.isPresent()) {
        String unhealthyMsg = String
            .format(UNHEALTHY_ES_PLUGIN_MSG, pluginName, badEsNodesOptional.get());
        LOGGER.error(unhealthyMsg);
        return Result.unhealthy(unhealthyMsg);
      } else {
        String healthyMsg = String.format(HEALTHY_ES_PLUGIN_MSG, pluginName);
        LOGGER.info(healthyMsg);
        return Result.healthy(healthyMsg);
      }

    } catch (IOException e) {
      LOGGER.error("I/O error while hitting Elasticsearch", e);
      return Result.unhealthy(UNHEALTHY_ELASTICSEARCH_MSG + e.getMessage());
    }
  }
}
