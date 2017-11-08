package gov.ca.cwds.rest.services.es;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.security.SecureClientFactory;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business service for Index Query.
 *
 * @author CWDS TPT-2
 */
public class IndexQueryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryService.class);

  private ElasticsearchConfiguration esConfig;

  /**
   * Constructor
   *
   * @param esConfig instance of ElasticsearchConfiguration with host and port assigned
   */
  @Inject
  public IndexQueryService(ElasticsearchConfiguration esConfig) {
    this.esConfig = esConfig;
  }

  public IndexQueryResponse handleRequest(IndexQueryRequest req) {
    checkArgument(req != null, "query cannot be Null or empty");
    @SuppressWarnings("unchecked")
    String query = new JSONObject((Map<String, String>) req.getQuery()).toString();
    if (StringUtils.isBlank(query)) {
      LOGGER.error("query cannot be null.");
      throw new DoraException("query cannot be null.");
    }
    return new IndexQueryResponse(searchIndexByQuery(req.getIndex(), req.getType(), query));
  }

  /**
   * Search given index by pass-through query.
   *
   * @param index index to search
   * @param type "person" or otherwise
   * @param query user-provided query
   * @return JSON ES results
   */
  private String searchIndexByQuery(final String index, final String type, final String query) {
    LOGGER.info("index: {}", index);
    LOGGER.info("type: {}", type);
    LOGGER.info("query: {}", query);
    checkArgument(!Strings.isNullOrEmpty(index), "index name cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(type), "type cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(query), "query cannot be Null or empty");

    final String targetURL = String.format("http://%s:%s/%s/%s/_search",
        esConfig.getHost().trim(), esConfig.getPort().trim(), index.trim(), type.trim());

    LOGGER.warn("ES SEARCH URL: {}", targetURL);
    return invokeElasticsearch(targetURL, query);
  }

  /**
   * Consume an external REST web service, specifying URL, request headers and Elasticsearch query
   *
   * @param targetURL the target URL
   * @param query the payload specified by user
   * @return the JSON payload returned by the external web service
   */
  String invokeElasticsearch(String targetURL, String query) {
    try {
      return postRequest(createElasticsearchRequest(targetURL), query);
    } catch (RuntimeException e) {
      final String msg = "Error while communicating with ElasticSearch: " + e.getMessage();
      LOGGER.error(msg, e);
      throw new DoraException(msg, e);
    }
  }

  private Builder createElasticsearchRequest(String targetURL) {
    Client client;
    if (esConfig.getXpack() != null && esConfig.getXpack().isEnabled()) {
      client = SecureClientFactory.createSecureClient();
    } else {
      client = ClientBuilder.newClient();
    }
    WebTarget target = client.target(targetURL);
    return target.request(MediaType.TEXT_PLAIN);
  }

  String postRequest(Builder request, String query) {
    return request.post(Entity.text(query.trim()), String.class);
  }
}
