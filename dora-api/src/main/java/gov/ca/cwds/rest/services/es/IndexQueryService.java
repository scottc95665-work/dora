package gov.ca.cwds.rest.services.es;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.managed.EsRestClientManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import javax.script.ScriptException;
import javax.ws.rs.HttpMethod;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import gov.ca.cwds.dora.security.FieldFilterScript;
import gov.ca.cwds.dora.security.FieldFilters;
import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.ElasticsearchException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.security.realm.PerrySubject;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.google.common.base.Preconditions.checkArgument;
import static gov.ca.cwds.dora.DoraUtils.getElasticSearchSearchResultCount;
import static gov.ca.cwds.dora.DoraUtils.getElasticSearchSearchTime;
import static gov.ca.cwds.dora.DoraUtils.stringToJsonMap;

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
  IndexQueryService() {
    // no op
  }

  public IndexQueryResponse handleRequest(IndexQueryRequest req) {
    checkArgument(req != null, "query cannot be Null or empty");
    try {
      long timeBeforeCallES = System.currentTimeMillis();
      Response response = callElasticsearch(req);
      LOGGER.debug("Dora took {} milliseconds to call Elasticsearch",
          System.currentTimeMillis() - timeBeforeCallES);

      InputStream content = response.getEntity().getContent();
      String esResponse = IOUtils.toString(content, StandardCharsets.UTF_8.toString());
      IOUtils.closeQuietly(content);

      Map<String, Object> esResponseJsonMap = stringToJsonMap(esResponse);

      LOGGER.debug("Elastic Search took {} milliseconds to execute the search",
          getElasticSearchSearchTime(esResponseJsonMap));
      LOGGER.debug("Elastic Search has {} results in total",
          getElasticSearchSearchResultCount(esResponseJsonMap));

      String filteredResponse;
      String documentType = req.getType();
      if (fieldFilters.hasFilter(documentType)) {
        LOGGER.debug("Applying field filtering for document type '{}'", documentType);
        filteredResponse = applyFieldFiltering(esResponseJsonMap, documentType);
      } else {
        LOGGER.debug("Field filtering for document type '{}' is not defined", documentType);
        filteredResponse = esResponse;
      }
      return new IndexQueryResponse(filteredResponse);
    } catch (ResponseException e) {
      throw new ElasticsearchException(e);
    } catch (IOException e) {
      throw new DoraException("Failed to call ES", e);
    }
  }

  String applyFieldFiltering(Map<String, Object> esResponseJsonMap, String documentType) {
    FieldFilterScript fieldFilterScript = fieldFilters.getFilter(documentType);
    IntakeAccount account = PerrySubject.getPerryAccount();
    try {
      return fieldFilterScript.filter(esResponseJsonMap, account);
    } catch (ScriptException e) {
      throw new DoraException(
          "Failed to apply Field Filtering for document type '" + documentType + "'", e);
    }
  }

  Response callElasticsearch(IndexQueryRequest req) throws IOException {
    final String index = req.getIndex();
    final String documentType = req.getType();
    final Object query = req.getQuery();
    checkArgument(!Strings.isNullOrEmpty(index), "index name cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(documentType), "type cannot be Null or empty");
    checkArgument(query != null, "query cannot be Null");

    @SuppressWarnings("unchecked")
    String queryString = new JSONObject((Map<String, String>) query).toString();
    checkArgument(!queryString.isEmpty(), "query cannot be empty");
    try {
      String endpoint = String.format("/%s/%s/_search", index.trim(), documentType.trim());
      return performRequest(endpoint, queryString);
    } catch (RuntimeException e) {
      throw new DoraException(e.getMessage(), e);
    }
  }

  Response performRequest(String endpoint, String queryString) throws IOException {
    InputStreamEntity entity = new InputStreamEntity(
        new ByteArrayInputStream(queryString.getBytes()));
    RestClient esRestClient = EsRestClientManager.getEsRestClient();
    if (esConfig.getXpack() != null && esConfig.getXpack().isEnabled()) {
      Header authHeader = new BasicHeader("Authorization", PerrySubject.getToken());
      return esRestClient.performRequest(HttpMethod.POST, endpoint, Collections.emptyMap(), entity,
          authHeader);
    } else {
      return esRestClient.performRequest(HttpMethod.POST, endpoint, Collections.emptyMap(), entity);
    }
  }
}
