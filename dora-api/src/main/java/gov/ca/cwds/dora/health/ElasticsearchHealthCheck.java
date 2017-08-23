package gov.ca.cwds.dora.health;

import com.google.inject.Inject;
import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import java.io.IOException;
import java.util.Map;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT2 Team
 */
public class ElasticsearchHealthCheck extends BasicDoraHealthCheck {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchHealthCheck.class);

  private static final String HEALTHY_ELASTICSEARCH_MSG = "Elasticsearch %s in cluster '%s' is up-and-running.";
  static final String UNHEALTHY_ELASTICSEARCH_MSG = "Can't connect to Elasticsearch. Details: ";

  /**
   * Constructor
   *
   * @param esConfig instance of ElasticsearchConfiguration
   */
  @Inject
  public ElasticsearchHealthCheck(ElasticsearchConfiguration esConfig) {
    super(esConfig);
  }

  @Override
  protected Result check() throws Exception {
    Result result = super.check();
    if (!result.isHealthy()) {
      return result;
    }

    try (RestClient esRestClient = DoraUtils.createElasticsearchClient(esConfig)) {
      Response response = esRestClient.performRequest("GET", "/");
      Map<String, Object> jsonMap = DoraUtils.responseToJsonMap(response);

      String version = DoraUtils.extractElasticsearchVersion(jsonMap);
      String clusterName = DoraUtils.extractElasticsearchClusterName(jsonMap);
      String healthyMsg = String.format(HEALTHY_ELASTICSEARCH_MSG, version, clusterName);

      LOGGER.info(healthyMsg);
      return Result.healthy(healthyMsg);

    } catch (IOException e) {
      LOGGER.error("I/O error while hitting Elasticsearch", e);
      return Result.unhealthy(UNHEALTHY_ELASTICSEARCH_MSG + e.getMessage());
    }
  }
}
