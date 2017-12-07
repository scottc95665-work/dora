package gov.ca.cwds.rest.services.es;

import static com.google.common.base.Preconditions.checkArgument;
import static gov.ca.cwds.dora.DoraUtils.getElasticSearchSearchResultCount;
import static gov.ca.cwds.dora.DoraUtils.getElasticSearchSearchTime;
import static gov.ca.cwds.dora.DoraUtils.stringToJsonMap;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import gov.ca.cwds.dora.security.FieldFilterScript;
import gov.ca.cwds.dora.security.FieldFilters;
import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.security.SecureClientFactory;
import gov.ca.cwds.security.realm.PerrySubject;
import java.util.Map;
import javax.script.ScriptException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
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

  @Inject
  private ElasticsearchConfiguration esConfig;

  @Inject
  private FieldFilters fieldFilters;

  /**
   * Constructor
   */
  public IndexQueryService() {
    // no op
  }

  public IndexQueryResponse handleRequest(IndexQueryRequest req) {
    checkArgument(req != null, "query cannot be Null or empty");
    @SuppressWarnings("unchecked")
    String query = new JSONObject((Map<String, String>) req.getQuery()).toString();
    if (StringUtils.isBlank(query)) {
      LOGGER.error("query cannot be null.");
      throw new DoraException("query cannot be null.");
    }
    String index = req.getIndex();
    String documentType = req.getType();
    LOGGER.info("User is searching for '{}' in Elasticsearch index '{}'", documentType, index);

    String esResponse = searchIndexByQuery(index, documentType, query);
    Map<String, Object> esResponseJsonMap = stringToJsonMap(esResponse);

    LOGGER.info("Elastic Search took {} milliseconds to execute the search",
        getElasticSearchSearchTime(esResponseJsonMap));
    LOGGER.info("Elastic Search has {} results in total",
        getElasticSearchSearchResultCount(esResponseJsonMap));

    String filteredResponse;
    FieldFilterScript fieldFilterScript = fieldFilters.getFilter(documentType);
    if (null == fieldFilterScript) {
      LOGGER.info("Field filtering for document type '{}' is not set", documentType);
      filteredResponse = esResponse;
    } else {
      LOGGER.info("Applying field filtering for document type '{}'", documentType);
      filteredResponse = applyFieldFiltering(esResponseJsonMap, documentType);
    }

    return new IndexQueryResponse(filteredResponse);
  }

  private String applyFieldFiltering(Map<String, Object> esResponseJsonMap, String documentType) {
    FieldFilterScript fieldFilterScript = fieldFilters.getFilter(documentType);
    IntakeAccount account = PerrySubject.getPerryAccount();
    try {
      return fieldFilterScript.filter(esResponseJsonMap, account);
    } catch (ScriptException e) {
      throw new DoraException(
          "Filed to apply Field Filtering for document type '" + documentType + "'", e);
    }
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
    checkArgument(!Strings.isNullOrEmpty(index), "index name cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(type), "type cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(query), "query cannot be Null or empty");

    final String targetURL = String.format("http://%s:%s/%s/%s/_search",
        esConfig.getHost().trim(), esConfig.getPort().trim(), index.trim(), type.trim());

    LOGGER.info("User searched {} in ElasticSearch", targetURL);
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
    Client client = null;
    try {
      if (esConfig.getXpack() != null && esConfig.getXpack().isEnabled()) {
        client = SecureClientFactory.createSecureClient();
      } else {
        client = ClientBuilder.newClient();
      }
      WebTarget target = client.target(targetURL);
      return postRequest(target.request(MediaType.TEXT_PLAIN), query);
    } catch (DoraException e) {
      throw e;
    } catch (RuntimeException e) {
      throw new DoraException(e.getMessage(), e);
    } finally {
      if (null != client) {
        client.close();
      }
    }
  }

  String postRequest(Builder request, String query) {
    Response response = request.post(Entity.text(query.trim()));
    if (HttpStatus.SC_OK == response.getStatus()) {
      return response.readEntity(String.class);
    } else {
      throw new DoraException(response.getStatusInfo().getReasonPhrase());
    }
  }
}
