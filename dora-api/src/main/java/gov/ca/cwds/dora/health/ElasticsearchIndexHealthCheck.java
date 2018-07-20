package gov.ca.cwds.dora.health;

import com.google.inject.Inject;
import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.EsRestClientManager;
import java.io.IOException;
import java.util.List;

import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public class ElasticsearchIndexHealthCheck extends ElasticsearchHealthCheck {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexHealthCheck.class);

  static final String ES_INDEXES_ENDPOINT = "/_cat/indices?format=json&pretty";

  static final String HEALTHY_ES_INDEX_MSG = "[%s] index exist on the server.";
  static final String UNHEALTHY_ES_INDEX_MSG = "[%s] index does not exist on the server.";

  private String indexName;

  @Inject
  private EsRestClientManager esRestClientManager;

  /**
   * Constructor
   *
   * @param esConfig instance of ElasticsearchConfiguration
   */
  @Inject
  public ElasticsearchIndexHealthCheck(ElasticsearchConfiguration esConfig, String indexName) {
    super(esConfig);
    this.indexName = indexName;
  }

  @Override
  protected Result check() throws Exception {
    Result result = super.check();
    if (!result.isHealthy()) {
      return result;
    }

    try {
      RestClient esRestClient = esRestClientManager.getEsRestClient();
      List<Object> jsonList = performRequestList(esRestClient, "GET", ES_INDEXES_ENDPOINT);

      if (!DoraUtils.isIndexExist(jsonList, indexName)) {
        String unhealthyMsg = String
            .format(UNHEALTHY_ES_INDEX_MSG, indexName);
        LOGGER.error(unhealthyMsg);
        return Result.unhealthy(unhealthyMsg);
      } else {
        String healthyMsg = String.format(HEALTHY_ES_INDEX_MSG, indexName);
        LOGGER.info(healthyMsg);
        return Result.healthy(healthyMsg);
      }

    } catch (IOException e) {
      LOGGER.error("I/O error while hitting Elasticsearch", e);
      return Result.unhealthy(UNHEALTHY_ELASTICSEARCH_MSG + e.getMessage());
    }
  }
}
