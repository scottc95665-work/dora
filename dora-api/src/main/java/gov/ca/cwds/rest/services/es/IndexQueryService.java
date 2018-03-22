package gov.ca.cwds.rest.services.es;

import static com.google.common.base.Preconditions.checkArgument;
import static gov.ca.cwds.dora.DoraUtils.getElasticSearchSearchResultCount;
import static gov.ca.cwds.dora.DoraUtils.getElasticSearchSearchTime;
import static gov.ca.cwds.dora.DoraUtils.stringToJsonMap;


import com.google.common.base.Strings;
import com.google.inject.Inject;
import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.dora.security.FieldFilterScript;
import gov.ca.cwds.dora.security.FieldFilters;
import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.security.realm.PerrySubject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import javax.script.ScriptException;
import javax.ws.rs.HttpMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
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
    String index = req.getIndex();
    String documentType = req.getType();
    LOGGER.info("User is searching for '{}' in Elasticsearch index '{}'", documentType, index);

    try {
      Response response = callElasticsearch(index, documentType, query);
      if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
        throw new DoraException(response.getStatusLine().getReasonPhrase());
      }

      InputStream content = response.getEntity().getContent();
      String esResponse = IOUtils.toString(content, StandardCharsets.UTF_8.toString());
      IOUtils.closeQuietly(content);

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

  Response callElasticsearch(String index, String documentType, String query) throws IOException {
    checkArgument(!Strings.isNullOrEmpty(index), "index name cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(documentType), "type cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(query), "query cannot be Null or empty");

    RestClient client = null;
    HttpHost[] httpHosts = DoraUtils.parseNodes(esConfig.getNodes());

    try {
      if (esConfig.getXpack() != null && esConfig.getXpack().isEnabled()) {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Authorization", PerrySubject.getToken());
        client = RestClient.builder(httpHosts).setDefaultHeaders(headers).build();
      } else {
        client = RestClient.builder(httpHosts).build();
      }

      StringEntity entity = new StringEntity(query, ContentType.APPLICATION_JSON);
      String endpoint = String.format("/%s/%s/_search", index.trim(), documentType.trim());
      return performRequest(client, entity, endpoint);
    } catch (RuntimeException e) {
      throw new DoraException(e.getMessage(), e);
    } finally {
      if (null != client) {
        client.close();
      }
    }
  }

  Response performRequest(RestClient client, StringEntity entity, String endpoint) throws IOException {
    return client.performRequest(HttpMethod.POST, endpoint, Collections.<String, String>emptyMap(), entity);
  }
}
