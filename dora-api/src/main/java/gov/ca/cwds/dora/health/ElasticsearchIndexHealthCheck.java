package gov.ca.cwds.dora.health;

import gov.ca.cwds.rest.DoraConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.dora.DoraUtils;

/**
 * @author CWDS TPT-2
 */
public class ElasticsearchIndexHealthCheck extends ElasticsearchHealthCheck {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexHealthCheck.class);

  static final String ES_INDEXES_ENDPOINT = "/_cat/indices?format=json&pretty";
  static final String ES_ALIASES_ENDPOINT = "/_cat/aliases?format=json&pretty";

  static final String HEALTHY_ES_INDEX_MSG = "[%s] index exist on the server.";
  static final String UNHEALTHY_ES_INDEX_MSG = "[%s] index does not exist on the server.";

  private String indexName;

  /**
   * Constructor
   *
   * @param config instance of DoraConfiguration
   */
  @Inject
  public ElasticsearchIndexHealthCheck(DoraConfiguration config, String indexName) {
    super(config);
    this.indexName = indexName;
  }

  @Override
  protected Result elasticsearchCheck(RestClient esRestClient) throws IOException {
    List<Object> jsonListIndices = performRequestList(esRestClient, "GET", ES_INDEXES_ENDPOINT);
    List<Object> jsonListAliases = getAliasesList(esRestClient, "GET", ES_ALIASES_ENDPOINT);


    if (DoraUtils.isIndexExist(jsonListIndices, indexName)
        || DoraUtils.isAliasExist(jsonListAliases, indexName)) {
      String healthyMsg = String.format(HEALTHY_ES_INDEX_MSG, indexName);
      LOGGER.info(healthyMsg);
      return Result.healthy(healthyMsg);
    } else {
      String unhealthyMsg = String.format(UNHEALTHY_ES_INDEX_MSG, indexName);
      LOGGER.error(unhealthyMsg);
      return Result.unhealthy(unhealthyMsg);
    }
  }

  private List<Object> getAliasesList(RestClient esRestClient, String method,
      String esAliasesEndpoint) throws IOException {
    List<Object> jsonListAliases = new ArrayList<>();
    try {
      jsonListAliases = performRequestList(esRestClient, method, esAliasesEndpoint);
    } catch (ResponseException re) {
      LOGGER.info("Aliases do not exist.");
    }
    return jsonListAliases;
  }
}
